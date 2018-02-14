<?php

     function enviarPasswd($email, $passwd){
        $to      = $email; // Send email to our user
        $subject = 'New Password'; // Give the email a subject 
        $message = '
        
        Tu nueva passwd es: '.$passwd.'
        Puedes volver a cambiarla en opciones.
        
        '; // Our message above including the link
                            
        $headers = 'From:djdany.djesus@gmail.com' . "\r\n"; // Set from headers
        mail($to, $subject, $message, $headers); // Send our email

     }

?>