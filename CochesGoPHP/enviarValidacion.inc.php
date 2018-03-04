<?php

     function enviarEmailValidacion($email, $hash){
        $to      = $email; // Send email to our user
        $subject = 'Signup | Verification'; // Give the email a subject 
        $message = '
        
        Thanks for signing up!
        Your account has been created, you have activated your account by pressing the url below.
        
        Please click this link to activate your account:
        http://www.yourwebsite.com/verificarEmail.php?email='.$email.'&hash='.$hash.'
        
        '; // Our message above including the link
                            
        $headers = 'From:djdany.djesus@gmail.com' . "\r\n"; // Set from headers
        mail($to, $subject, $message, $headers); // Send our email

     }

?>