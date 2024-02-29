<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta http-equiv="X-UA-Compatible" content="IE=edge">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>YO SWAPI PAGES</title>
</head>
<style>
  body {
    text-align: center;
  }
  table {
    border-collapse: collapse;
    border-spacing: 0;
    margin: auto;
    margin-top: 20px;
    border: 1px solid #ddd;
  }
  table thead td, table tbody td{
    border: 1px solid #ddd;
  }
  th, td {
    text-align: left;
    padding: 16px;
  }

  tr:nth-child(even) {
    background-color: #f2f2f2;
  }

  input {
    margin-bottom: 10px;
  }
</style>
<body>
  <h1>WELCOME</h1>
  <label for="">Search your starwars name</label>
  <form action="<?=$_SERVER['PHP_SELF']; ?>" method="post">
    <input type="text" name="q" id="" placeholder="Luke"><br>
    <input type="submit" value="Search" name="isSubmit">
  </form>
</body>
</html>

<?php
  if(isset($_POST['isSubmit']) == "Search"){
    $q = $_POST['q'];

    $curl = curl_init();

    curl_setopt_array($curl, array(
      CURLOPT_URL => 'http://localhost:8989/people/find?name=' . $q,
      CURLOPT_RETURNTRANSFER => true,
      CURLOPT_HTTP_VERSION => CURL_HTTP_VERSION_1_1,
      CURLOPT_CUSTOMREQUEST => 'POST',
    ));

    
    $response = curl_exec($curl);

    curl_close($curl);
    
    $result = json_decode($response);

    if(isset($result) == NULL){
      echo "Oops, something went off.";
      die();
    }

    if(isset($result->status) == "500"){
      echo "Oops, Network not available";
      die();
    }
    
    if(count($result) == 0){
      echo "not found any names like " . $q;
      die();
    }
    $tbResult = "";
    foreach ($result as $person) {
      $tbResult .= "<tr>";
      $tbResult .= "<td>$person->name</td>";
      $tbResult .= "<td>$person->gender</td>";
      $tbResult .= "<td><a href='" . $person->homeworld . "'> Link </a></td>";

      $tbStarship = "";
      if($person->starshipDetail !== null) {
        foreach ($person->starshipDetail as $ship) {
          $tbStarship .= "Starship name: " . $ship->name . " <br> model: " . $ship->model . "<br>";
          # code...
        }
      } else {
        $tbStarship = "-";
      }
      $tbResult .= "<td>$tbStarship</td>";
      $tbResult .= "</tr>";
    }

    echo "<table>
          <thead>
            <td>Name</td>
            <td>Gender</td>
            <td>Homeworld</td>
            <td>Starships</td>
          </thead>  
          <tbody>
              $tbResult
          </tbody>
        </table>";

    
  }
?>