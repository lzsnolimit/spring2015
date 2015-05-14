<!DOCTYPE html>
<?php
	require_once('php/register.php');
?>
<html>
<head>
	<meta charset="utf-8">
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<title>Home Page</title>
	<link rel="stylesheet" href="http://maxcdn.bootstrapcdn.com/bootstrap/3.2.0/css/bootstrap.min.css">
	<link type="text/css" rel="stylesheet" href="css/home_css.css">
	<script src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.3/jquery.min.js"></script>
	<script src="js/login_signup.js"></script>
</head>
<body>
<nav class="navbar navbar-default" id="header">
	<div class="container">
		<div class="navbar-header">
			<img class="navbar-brand" src="image/logo.png" id="logo">
		</div>
	</div>
</nav>
<div class="container">
	<div class="row">
		<div class="col-sm-6 col-md-6 col-lg-8" id="section">
			<h1>Welcome to wyoExchange</h1>
			<p>wyoExchange allows you to exchange textbooks with students to save money.</p>
			<p>You can connect with other students to exchange skills and knowledge.</p>
		</div>
		<div class="col-sm-6 col-md-6 col-lg-4" id="aside">
			<h3>Log In <small>If you have an wyoExchange account!</small></h3>
			<form role="form" id="login_form" action="#" method="post">
				<div class="form-group">
					<label class="sr-only" for="email">Enter your UW Email</label>
					<input type="email" class="form-control" placeholder="UW Email" name="login_email" id="login_email">
					<span></span>
				</div>
				<div class="form-group">
					<label class="sr-only" for="pwd">Enter your Password</label>
					<input type="password" class="form-control" placeholder="Password" name="login_password" id="login_password">
					<span></span>
				</div>
				<div class="form-group">
					<label>
						<a href="passwordReSet.php">Forgot password? </a>
						<input type="button" class="btn btn-default" id="log_in" value="Log In">
					</label>
				</div>
			</form>
			<h3>New at wyoExchange? <small>Sign Up now!</small></h3>
			<form role="form" id="register_form" action="#" method="post">
				<div class="form-group">
					<label class="sr-only" for="text">Please enter your first name</label>
					<input type="text" class="form-control" placeholder="First Name" name="first_name" id="first_name">
					<span></span>
				</div>
				<div class="form-group">
					<label class="sr-only" for="text">Please enter your last name</label>
					<input type="text" class="form-control" placeholder="Last Name" name="last_name" id="last_name">
					<span></span>
				</div>
				<div class="form-group">
					<label class="sr-only" for="email">Please enter your uw email</label>
					<input type="email" class="form-control" placeholder="UW Email" name="register_email" id="register_email">
					<span><?php echo $emailExist; ?></span>
				</div>
				<div class="form-group">
					<label class="sr-only" for="pwd">Please enter your password for wyoExchange account</label>
					<input type="password" class="form-control" placeholder=" New Password" name="register_password" id="register_password">
					<span></span>
				</div>
				<input type="button" class="btn btn-default" id="sign_up" value="Sign Up">
			</form>
		</div>
	</div>
</div>
</body>
</html>