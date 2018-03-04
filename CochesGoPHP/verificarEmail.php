<?php

        include 'config.inc.php';

        if(isset($_GET['email']) && !empty($_GET['email']) AND isset($_GET['hash']) && !empty($_GET['hash'])){
            // Verify data
            $email = $_GET['email']; // Set email variable
            $hashValidacion = $_GET['hash']; // Set hash variable
                         
            // Query database for row exist or not
                $sql = 'SELECT count(*) FROM usuarios 
                WHERE email = :email and hashValidacion= :hashValidacion; and active=0';
                $stmt = $conn->prepare($sql);
                $stmt->bindParam(':email', $email, PDO::PARAM_STR);
                $stmt->bindParam(':hashValidacion', $hashValidacion, PDO::PARAM_STR);
                $stmt->execute();
                $numRow=$stmt->fetchColumn();

                if($numRow==1)
                {
                    $sql='UPDATE usuarios SET activo=1 WHERE email=:email';
                    $stmt = $conn->prepare($sql);
                    $stmt->bindParam(':email', $email, PDO::PARAM_STR);
                    $stmt->execute();
                    echo '<div class="statusmsg">Your account has been activated, you can now login</div>';
                }  
                else
                {
                    echo '<div class="statusmsg">The url is either invalid or you already have activated your account.</div>';
                }
            
            }
     

?>