<?php
	//database connection for wyoexchange
	$dsn = 'mysql:host=localhost;dbname=myuwyo';
	$userName = 'root';
	$password = '';
	try{
		$db = new PDO($dsn, $userName, $password);
	}catch(PDOException $e){
		$errorMessage = $e->getMessage();
		include('database_error.php');
		exit();
	}
?>