package com.reddot.restapi.dto;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PersonDTO implements Serializable {
  private String name;
  private String gender;
  private String homeworld;
  private String[] starships;
  private List<StarshipDTO> starshipDetail;
  
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }
  public String getGender() {
    return gender;
  }
  public void setGender(String gender) {
    this.gender = gender;
  }
  public String getHomeworld() {
    return homeworld;
  }
  public void setHomeworld(String homeworld) {
    this.homeworld = homeworld;
  }
  public String[] getStarships() {
    return starships;
  }
  public void setStarships(String[] starships) {
    this.starships = starships;
  }
  public List<StarshipDTO> getStarshipDetail() {
    return starshipDetail;
  }
  public void setStarshipDetail(List<StarshipDTO> starshipDetail) {
    this.starshipDetail = starshipDetail;
  }
}
