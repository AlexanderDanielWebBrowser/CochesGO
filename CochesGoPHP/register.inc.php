<?php

     include 'config.inc.php';
	 
	 // Check whether username or email or password is set from android	
     if(isset($_POST['usuario']) && isset($_POST['email']) && isset($_POST['passwd']))
     {
		  // Innitialize Variable
		  $result='';
	   	  $usuario = $_POST['usuario'];
          $email = $_POST['email'];
            $passwd = $_POST['passwd'];
		    if(filter_var($email, FILTER_VALIDATE_EMAIL)){
		  // Query database for row exist or not
          $sql = 'INSERT INTO usuarios (email, username, passwd) VALUES (:email, :usuario, :passwd);';
          $stmt = $conn->prepare($sql);
          $stmt->bindParam(':usuario', $usuario, PDO::PARAM_STR);
          $stmt->bindParam(':email', $email, PDO::PARAM_STR);
          $stmt->bindParam(':passwd', $passwd, PDO::PARAM_STR);
          $stmt->execute();
          
          $result='true';
		  } else {
        $result='false';
      }
		  // send result back to android
   		  echo $result;
  	} else {
      echo "No hay Params.";
    }
	
?>