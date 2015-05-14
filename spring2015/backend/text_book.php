<?php
include_once ("PHPMailer-master/class.phpmailer.php");
include_once ("PHPMailer-master/class.smtp.php");
session_start ();
if (! isset ( $_GET ['type'] )) {
	echo "wrong";
	exit ();
}

$con = mysql_connect ( "localhost", "lzsnolimit", "139444" );
if (! $con) {
	die ( 'Could not connect: ' . mysql_error () );
}
mysql_select_db ( 'lzsnolimit', $con ) or die ( 'Can\'t use myuwyo : ' . mysql_error () );
switch ($_GET ['type']) {
	case 0 :
		// get course list
		print_r ( queryCourseLiset () );
		break;
	
	case 1 :
		// query textbook
		if (! isset ( $_GET ['id'] )) {
			return;
		}
		print_r ( queryById ( $_GET ['id'] ) );
		break;
	
	case 2 :
		// require textbook
		if (! isset ( $_GET ['user'] ) || ! isset ( $_GET ['pass'] ) || ! isset ( $_GET ['id'] )) {
			return;
		}
		requestBook ( $_GET ['user'], $_GET ['pass'], $_GET ['id'] );
		break;
	
	case 3 :
		// donate textbook
		// donate($_GET['user'],$_GET['pass'],$_GET['id'],$_GET['email'],$_GET['phone']);
		if (! isset ( $_GET ['user'] ) || ! isset ( $_GET ['pass'] ) || ! isset ( $_GET ['id'] ) || ! isset ( $_GET ['phone'] ) || ! isset ( $_GET ['email'] )) {
			echo "wrong";
			return false;
		}
		donate ( $_GET ['user'], $_GET ['pass'], $_GET ['id'], $_GET ['email'], $_GET ['phone'] );
		break;
	
	case 4 :
		if (! isset ( $_GET ['name'] ) || ! isset ( $_GET ['pass'] )) {
			exit ();
		} else {
			if (login ( $_GET ['name'], $_GET ['pass'] ) <= 0) {
				echo 0;
			} else {
				echo json_encode ( $_SESSION ['user'] );
			}
		}
		break;
	
	case 5 :
		if (! isset ( $_GET ['email'] ) || ! isset ( $_GET ['pass'] )|| ! isset ( $_GET ['first_name'] )|| ! isset ( $_GET ['last_name'] ))
		{
			exit();
		}
		else
		{
			signUp($_GET ['email'],$_GET ['pass'],$_GET ['first_name'],$_GET ['last_name']);
		}
		break;
	
	default :
		echo "wrong";
}
mysql_close ( $con );
function queryCourseLiset() {
	$sql = "SELECT `id`, `courseDepartment`, `courseId`, `courseSection` FROM `textbook` WHERE 1";
	$results = mysql_query ( $sql );
	$courses = array ();
	while ( $row = mysql_fetch_array ( $results ) ) {
		array_push ( $courses, $row );
	}
	return json_encode ( $courses );
}
function queryById($id) {
	$sql = "SELECT `id`, `instructure`, `courseDepartment`, `courseId`, `courseSection` FROM `textbook` WHERE `id` = " . $id;
	$results = mysql_query ( $sql );
	if (mysql_affected_rows () <= 0) {
		echo "";
		exit ();
	}
	$results = mysql_fetch_array ( $results );
	$sql = "SELECT * FROM `shared_books` WHERE `textbookid` = " . $results ['id'] . " AND `status` = 0";
	$result = mysql_query ( $sql );
	if (mysql_affected_rows () <= 0) {
		$results ["sharedId"] = 0;
	} else {
		$sharedBook = array ();
		$sharedBook = mysql_fetch_array ( $result );
		$results ["sharedId"] = $sharedBook ["id"];
	}
	echo json_encode ( $results );
}
function requestBook($user, $pass, $id) {
	
	// echo "hahha";
	$sql = "SELECT * FROM `shared_books` WHERE `id` = " . $id . " AND `status` = 0";
	mysql_query ( $sql );
	if (mysql_affected_rows () <= 0) {
		$result = array ();
		$result [0] = 1;
		$result [1] = "This textbook is not available!";
		echo json_encode ( $result );
		exit ();
	}
	
	$sharebook = mysql_fetch_array ( mysql_query ( $sql ) );
	$loginResult = login ( $user, $pass );
	if ($loginResult <= 0) {
		$result = array ();
		$result [0] = 2;
		$result [1] = "Please login in!";
		echo json_encode ( $result );
		exit ();
	} else {
		if ($_SESSION ['user'] ['credit'] <= 0) {
			// $_SESSION['user']['credit']--;
			$result = array ();
			$result [0] = 3;
			$result [1] = "You don't have enough credits";
			echo  json_encode($result);
			exit ();
		}
		
		$sql = "SELECT * FROM `users` WHERE `id` = " . $sharebook ["uploaderid"];
		$donator = mysql_fetch_array ( mysql_query ( $sql ) );
		$_SESSION ['user'] ['credit'] --;
		$donator ['credit'] ++;
		$sql = "UPDATE `myuwyo`.`users` SET `credit` = '" . $donator ['credit'] . "' WHERE `users`.`id` = " . $donator ['id'] . ";";
		mysql_query ( $sql );
		// $sql="DELETE FROM `shared_books` WHERE `id` = ".$sharebook['id'];
		$sql = "UPDATE `myuwyo`.`shared_books` SET `status` = '1' WHERE `shared_books`.`id` = " . $id . ";";
		// echo $sql;
		
		mysql_query ( $sql );
		$sql = "SELECT `id`, `instructure`, `courseDepartment`, `courseId`, `courseSection` FROM `textbook` WHERE id=" . $sharebook ["textbookid"];
		// echo $sql;
		$shareBookDetail = mysql_fetch_array ( mysql_query ( $sql ) );
		// echo $_SESSION['user']['books_require'];
		if ($_SESSION ['user'] ['books_require'] == null) {
			$books = array ();
			array_push ( $books, $shareBookDetail );
		} else {
			$books = json_decode ( $_SESSION ['user'] ['books_require'], true );
			// print_r($shareBookDetail);
			array_push ( $books, $shareBookDetail );
		}
		$_SESSION ['user'] ['books_require'] = $books;
		// print_r($_SESSION['user']['books_require']);
		$sql = "UPDATE `myuwyo`.`users` SET `credit` = '" . $_SESSION ['user'] ['credit'] . "', `books_require` = '" . json_encode ( $_SESSION ['user'] ['books_require'] ) . "' WHERE `users`.`id` = " . $_SESSION ['user'] ['id'] . ";";
		mysql_query ( $sql );
		
		//echo "Please check your email!";
		// $sharebook[""]
		$emailContent = "Please contact the donator with the following information:</br> Email:" . $sharebook ["email"] . "</br>Phone:" . $sharebook ["phone"];
		send_email ( "lzsnolimit@gmail.com", $emailContent, $_SESSION ['user'] ['first_name'] . "  " . $_SESSION ['user'] ['last_name'] );
		$result = array ();
		$result [0] = 0;
		$result [1] = $sharebook;
		echo  json_encode($result);
	}
}
function signUp($user, $pass,$first_name,$last_name) {
	$sql = "SELECT * FROM `users` WHERE `email` = '" . $user . "'";
	mysql_query ( $sql );
	if (mysql_affected_rows () >= 1) {
		echo "0";
	} else {
		$sql="INSERT INTO `myuwyo`.`users` (`id`, `first_name`, `last_name`, `email`, `pass`, `credit`, `pic`, `books_donate`, `books_require`) VALUES (NULL, '".$first_name."', '".$last_name."', '".$user."', '".md5($pass)."', '0', '', '', '');";
		mysql_query ( $sql );
		echo 1;
	}
}
function donate($user, $pass, $id, $email, $phone) {
	// echo "wrong";
	$uploader = login ( $user, $pass );
	if ($uploader == - 1) {
		echo "Wrong user or password!";
		return false;
	}
	
	// if($_SESSION['user']['books_donate']==null)
	// {
	// $books=array();
	// array_push($books,$sharebook);
	// }
	// else
	// {
	// $books=json_decode($_SESSION['user']['books_require'],true);
	// array_push($books,$sharebook);
	// }
	$sql = "INSERT INTO `myuwyo`.`shared_books` (`id`, `uploaderid`, `upload_time`, `email`, `phone`, `textbookid`) VALUES (NULL, '" . $uploader . "', '" . time () . "', '" . $email . "', '" . $phone . "', '" . $id . "')";
	mysql_query ( $sql );
	
	$sql = "SELECT `id`, `instructure`, `courseDepartment`, `courseId`, `courseSection` FROM `textbook` WHERE id=" . $id;
	// echo $sql;
	$shareBookDetail = mysql_fetch_array ( mysql_query ( $sql ) );
	if ($_SESSION ['user'] ['books_donate'] == null) {
		$books = array ();
		// print_r($shareBookDetail);
		array_push ( $books, $shareBookDetail );
	} else {
		$books = json_decode ( $_SESSION ['user'] ['books_donate'], true );
		// print_r($shareBookDetail);
		array_push ( $books, $shareBookDetail );
	}
	$_SESSION ['user'] ['books_donate'] = $books;
	$sql = "UPDATE `myuwyo`.`users` SET `books_donate` = '" . json_encode ( $_SESSION ['user'] ['books_donate'] ) . "' WHERE `users`.`id` = " . $uploader . ";";
	mysql_query ( $sql );
	echo "Done";
}
function login($user, $pass) {
	$sql = "SELECT * FROM `users` WHERE `email` LIKE '" . $user . "' AND `pass` LIKE '" . md5 ( $pass ) . "';";
	$results = mysql_query ( $sql );
	if (mysql_affected_rows () <= 0) {
		return - 1;
	} else {
		$_SESSION ['user'] = mysql_fetch_array ( $results );
		// $_SESSION['user']=$row;
		$_SESSION ['user'] ['books_donate'] = stripslashes ( $_SESSION ['user'] ['books_donate'] );
		$_SESSION ['user'] ['books_require'] = stripslashes ( $_SESSION ['user'] ['books_require'] );
		return $_SESSION ['user'] [0];
	}
}
function send_email($receiver, $content, $name) {
	
	// ��ȡһ���ⲿ�ļ�������
	$mail = new PHPMailer ();
	// $body = file_get_contents('contents.html');
	// $body = eregi_replace("[\]",'',$body);
	// ����smtp����
	$mail->IsSMTP ();
	$mail->SMTPAuth = true;
	$mail->SMTPKeepAlive = true;
	$mail->SMTPSecure = "ssl";
	$mail->Host = "smtp.gmail.com";
	$mail->Port = 465;
	$mail->Username = "lzsnolimit@gmail.com";
	$mail->Password = "pojieshibai";
	$mail->From = "lzsnolimit@gmail.com";
	$mail->FromName = "Exchange";
	$mail->Subject = "This is the subject";
	$mail->AltBody = "$content";
	$mail->WordWrap = 50; // set word wrap
	$mail->MsgHTML ( $content );
	$mail->AddReplyTo ( "lzsnolimit@gmail.com", "Webmaster" );
	
	// $mail->AddAttachment("attachment.jpg");
	// $mail->AddAttachment("attachment.zip");
	$mail->AddAddress ( $receiver, $name );
	$mail->IsHTML ( true );
	if (! $mail->Send ()) {
		//echo "Mailer Error: " . $mail->ErrorInfo;
	} else {
		//echo "Message has been sent";
	}
}

?>