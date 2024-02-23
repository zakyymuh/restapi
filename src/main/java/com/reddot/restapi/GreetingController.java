package com.reddot.restapi;

import java.util.concurrent.atomic.AtomicLong;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

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

  @PostMapping("/people/find")
  public String find(@RequestParam(value = "name") String name) {
    String url = "https://swapi.dev/api/people?search=%s";
    RestTemplate restTemplate = new RestTemplate();
    String result = restTemplate.getForObject(String.format(url, name), String.class);
    return result;
  }

  @PostMapping("/planets")
  public String planets(@RequestParam(value = "id") String id){
    String url = "https://swapi.dev/api/planets/%s";
    RestTemplate restTemplate = new RestTemplate();
    String result = restTemplate.getForObject(String.format(url, id), String.class);
    return result;
  }
}
