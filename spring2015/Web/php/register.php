<?php
	//connect to the database
	require_once('user_connect.php');
	
	//get user input
	$firstName = filter_input(INPUT_POST, 'first_name');
	$lastName = filter_input(INPUT_POST, 'last_name');
	$email = filter_input(INPUT_POST, 'register_email', FILTER_VALIDATE_EMAIL);
	$password = filter_input(INPUT_POST, 'register_password');
	
	//chech whether user already exist or not
	$query = 'SELECT * FROM users WHERE email = :email_id';
	$statement = $db->prepare($query);
	$statement->bindValue(':email_id', $email);
	$statement->execute();
	$emailResult = $statement->fetch();
	$statement->closeCursor();
	
	$emailExist = '';
	if($emailResult !== FALSE){
		$emailExist = $emailResult['email'].' already exist! Please use different uwyo email';
		//exit();
	}else{
		//store the information in database
		$credits = 1;
		$insertQuery = 'INSERT INTO users (first_name, last_name, email, pass, credits) VALUES (:first, :last, :email_id, :password, :cred)';
		$insertStatement = $db->prepare($insertQuery);
		$insertStatement->bindValue(':first', $firstName);
		$insertStatement->bindValue(':last', $lastName);
		$insertStatement->bindValue(':email_id', $email);
		$insertStatement->bindValue(':password', $password);
		$insertStatement->bindValue(':cred', $credits);
		$insertStatement->execute();
		$insertStatement->closeCursor();
		//redirect the user to a customized homepage
		
	}
?>