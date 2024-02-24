package com.reddot.restapi;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.catalina.connector.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.boot.json.JsonParser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.reddot.restapi.dto.Car;
import com.reddot.restapi.dto.PersonDTO;
import com.reddot.restapi.dto.PlanetDTO;
import com.reddot.restapi.dto.StarshipDTO;

@RestController
public class GreetingController {
  public static final String template = "Hello, %s!";
  private final AtomicLong counter = new AtomicLong();

  @GetMapping("/greeting")
  public Greeting greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
    return new Greeting(counter.incrementAndGet(), String.format(template, name));
  }

  @PostMapping("/people")
  public String people() {
    String url = "https://swapi.dev/api/people/100";
    RestTemplate restTemplate = new RestTemplate();
    String result = restTemplate.getForObject(url, String.class);
    return result;
  }

  @PostMapping("/person/find")
  public ResponseEntity find(@RequestParam(value = "name") String name) {
    String url = "https://swapi.dev/api/people?search=%s";
    RestTemplate restTemplate = new RestTemplate();
    String result = restTemplate.getForObject(String.format(url, name), String.class);

    try {

      JSONObject jsonObject = new JSONObject(result);
      JSONArray jsonArray = jsonObject.getJSONArray("results");
      // JSONArray jsonarray = jsonObject.getJSONArray("result");

      ObjectMapper objectMapper = new ObjectMapper();
      List<PersonDTO> person = objectMapper.readValue(jsonArray.toString(), new TypeReference<List<PersonDTO>>() {
      });

      for (int i = 0; i < person.size(); i++) {
        String[] starships = person.get(i).getStarships();
        person.get(i).setStarshipDetail(getStarships(starships));
        System.out.println(person.get(i).getHomeworld());
        try {
          URI uri = new URI(person.get(i).getHomeworld());
          String path = uri.getPath();
          // String idStr = path.substring(path.lastIndexOf('/') - 1);
          // int id = Integer.parseInt(idStr);
          String[] arrOfStr = path.split("/");
          String id = arrOfStr[arrOfStr.length - 1];
          person.get(i).setHomeworld("http://localhost:8080/planets?id=" + id);
        } catch (Exception e) {
          // TODO: handle exception
          System.out.println(e.getMessage());
          System.out.println("err");
        }
      }

      return ResponseEntity.ok(person);
      // return result;
    } catch (IOException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  @PostMapping("/car")
  public String car() {
    ObjectMapper objectMapper = new ObjectMapper();

    String jsonArray = "[{\"brand\":\"ford\"}, {\"brand\":\"Fiat\"}]";

    try {
      List<Car> cars1 = objectMapper.readValue(jsonArray, new TypeReference<List<Car>>() {
      });
      System.out.println(cars1);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return "ok";
  }

  @GetMapping("/planets")
  public ResponseEntity planets(@RequestParam(value = "id") String id) {
    String url = "https://swapi.dev/api/planets/%s";
    RestTemplate restTemplate = new RestTemplate();
    String result = restTemplate.getForObject(String.format(url, id), String.class);

    ObjectMapper objectMapper = new ObjectMapper();
    JSONObject jsonObject = new JSONObject(result);
    String name = jsonObject.getString("name");
    String population = jsonObject.getString("population");
    PlanetDTO planet = new PlanetDTO();
    planet.setName(name);
    planet.setPopulation(population);

    return ResponseEntity.ok().body(planet);
  }

  public List<StarshipDTO> getStarships(String[] starships) {
    RestTemplate restTemplate = new RestTemplate();

    // String[] starshipData = new String[starships.length];
    List<StarshipDTO> starshipData = new ArrayList<StarshipDTO>();
    if (starships.length == 0) {
      return null;
    } else {
      for (int i = 0; i < starships.length; i++) {
        String result = restTemplate.getForObject(starships[i], String.class);
        JSONObject jsonObject = new JSONObject(result);

        StarshipDTO starship = new StarshipDTO();
        starship.setName(jsonObject.getString("name"));
        starship.setModel(jsonObject.getString("model"));

        starshipData.add(starship);
      }
      return starshipData;
    }
    // String url = "https://swapi.dev/api/starships/%s";
  }
}
