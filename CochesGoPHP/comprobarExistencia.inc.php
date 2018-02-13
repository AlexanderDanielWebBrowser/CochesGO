<?php

     function comprobarExistencia($usuario, $email){
        include 'config.inc.php';
        // Query database for row exist or not
        $sql = 'SELECT count(*) FROM usuarios 
                WHERE username = :usuario OR email = :email;';
        $stmt = $conn->prepare($sql);
        $stmt->bindParam(':usuario', $usuario, PDO::PARAM_STR);
        $stmt->bindParam(':email', $email, PDO::PARAM_STR);
        $stmt->execute();
        $numRow=$stmt->fetchColumn();

        if($numRow==1)
        {
            return false;
        }  
        else
        {
            return true;
        }
     }
	
?>