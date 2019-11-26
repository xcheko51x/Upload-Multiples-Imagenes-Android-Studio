<?php
   
    $nomImagen = $_POST['nom'];
    $imagen = $_POST['imagenes'];
    
    // RUTA DONDE SE GUARDARAN LAS IMAGENES
    $path = "productos/$nomImagen.png";
    $actualpath = "http://localhost/PruebasCanal/$path";
    
    file_put_contents($path, base64_decode($imagen));
    
    echo "SE SUBIO EXITOSAMENTE";
    
?>
