<?php

     include 'config.inc.php';
     include 'comprobarExistencia.inc.php';
     include 'enviarPasswd.inc.php';
	 
	 // Check whether username or email or password is set from android	
     if(isset($_POST['usuario']) && isset($_POST['email']) && isset($_POST['passwdSIN']) && isset($_POST['passwd']))
     {
          // Innitialize Variable
          $usuario = $_POST['usuario'];
          $email = $_POST['email'];
            $passwd = $_POST['passwd'];
            $passwdSIN = $_POST['passwdSIN'];
		    if(!comprobarExistencia($usuario, $email)){
		  // Query database for row exist or not
          $sql = 'UPDATE usuarios SET passwd = :passwd 
                  WHERE email=:email OR username=:usuario;';
          $stmt = $conn->prepare($sql);
          $stmt->bindParam(':usuario', $usuario, PDO::PARAM_STR);
          $stmt->bindParam(':email', $email, PDO::PARAM_STR);
          $stmt->bindParam(':passwd', $passwd, PDO::PARAM_STR);
          $stmt->execute();
          
          enviarPasswd($email, $passwdSIN);

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