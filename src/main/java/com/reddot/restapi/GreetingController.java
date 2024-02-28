package com.reddot.restapi;

import java.io.IOException;

import java.net.URI;
import java.util.ArrayList;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reddot.restapi.dto.PersonDTO;
import com.reddot.restapi.dto.PlanetDTO;
import com.reddot.restapi.dto.StarshipDTO;

@RestController
public class GreetingController {
	public static final String BASE_URL = "https://swapi.co/api/";
	public static final String PEOPLE_URL = "https://swapi.co/api/people?search=%s";
	public static final String PLANETS_URL = "https://swapi.co/api/planets/%s";
  public static final String HOME_URL = "http://localhost:8989";

  @PostMapping("/people/find")
  public ResponseEntity find(@RequestParam(value = "name") String name) {
    
    Cache cache = new Cache();
    boolean isExist = cache.isExist("person", name);
    String result = null;

    if (isExist) {
      result = cache.read("person", name);
    } else {
      RestTemplate restTemplate = new RestTemplate();
      result = restTemplate.getForObject(String.format(PEOPLE_URL, name), String.class);
      cache.write("person", name, result);
    }

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

        try {
          URI uri = new URI(person.get(i).getHomeworld());
          String path = uri.getPath();

          String[] arrOfStr = path.split("/");
          String id = arrOfStr[arrOfStr.length - 1];

          person.get(i).setHomeworld(HOME_URL + "/planets?id=" + id);

        } catch (Exception e) {
          // TODO: handle exception
          System.out.println(e.getMessage());
        }
      }

      return ResponseEntity.ok(person);
      // return result;
    } catch (IOException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  @GetMapping("/planets")
  public ResponseEntity planets(@RequestParam(value = "id") String id) {
    String url = "https://swapi.dev/api/planets/%s";

    Cache cache = new Cache();
    boolean isExist = cache.isExist("planets", id);
    String result = null;

    if (isExist) {
      result = cache.read("planets", id);
    } else {
      RestTemplate restTemplate = new RestTemplate();
      result = restTemplate.getForObject(String.format(PLANETS_URL, id), String.class);
      cache.write("planets", id, result);
    }

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
        Cache cache = new Cache();

        String id = splitId(starships[i]);
        boolean isExist = cache.isExist("starship", id);
        String result = null;

        if(isExist) {
          result = cache.read("starship", id);
        } else {
          result = restTemplate.getForObject(starships[i], String.class);
          cache.write("starship", id, result);
        }

        JSONObject jsonObject = new JSONObject(result);

        StarshipDTO starship = new StarshipDTO();
        starship.setName(jsonObject.getString("name"));
        starship.setModel(jsonObject.getString("model"));

        starshipData.add(starship);
      }
      return starshipData;
    }
  }

  public String splitId(String id) {
    try {
      URI uri = new URI(id);
      String path = uri.getPath();

      String[] arrOfStr = path.split("/");
      String res = arrOfStr[arrOfStr.length - 1];
      return res;
    } catch (Exception e) {
      System.out.println(e.getMessage());
      return null;
    }
  }

}
