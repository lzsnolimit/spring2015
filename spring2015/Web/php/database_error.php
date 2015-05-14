<!DOCTYPE html>
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
			<img class="navbar-brand" src="../image/logo.png" id="logo">
		</div>
	</div>
</nav>
	<div class="container">
		<h3>Database Connection Error!</h3>
		<p>There is a problem while connecting to the database.</p>
		<p>Error: <?php echo $errorMessage; ?></php></p>
	</div>
</body>
</html>