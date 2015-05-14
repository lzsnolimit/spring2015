$(document).ready(function(){
	$("#log_in").click(function(){
		var email = $("#login_email").val();
		var password = $("#login_password").val();
		var isValid = true;
		
		if(email == ""){
			$("#login_email").next().text("uwyo email is required!");
			isValid = false;
		}else{
			var n = email.search(/uwyo.edu/i);
			if(n == -1){
				$("#login_email").next().text("uwyo email is required!");
				isValid = false;
			}else{
				$("#login_email").next().text("");
			}
		}
		
		if(password == ""){
			$("#login_password").next().text("Password is required!");
			isValid = false;
		}else{
			$("#login_password").next().text("");
		}
		
		if(isValid){
			$("#login_form").submit();
		}
	});
	
	$("#sign_up").click(function(){
		var first_name = $("#first_name").val();
		var last_name = $("#last_name").val();
		var email = $("#register_email").val();
		var password = $("#register_password").val();
		var isValid = true;
		
		if(first_name == ""){
			$("#first_name").next().text("First name is required!");
			isValid = false;
		}else{
			$("#first_name").next().text("");
		}
		
		if(last_name == ""){
			$("#last_name").next().text("Last name is required!");
			isValid = false;
		}else{
			$("#last_name").next().text("");
		}
		
		if(email == ""){
			$("#register_email").next().text("uwyo email is required!");
			isValid = false;
		}else{
			var n = email.search(/uwyo.edu/i);
			if(n == -1){
				$("#register_email").next().text("uwyo email is required!");
				isValid = false;
			}else{
				$("#register_email").next().text("");
			}
		}
		
		if(password == ""){
			$("#register_password").next().text("Password is required!");
			isValid = false;
		}else{
			$("#register_password").next().text("");
		}
		
		if(isValid){
			$("#register_form").submit();
		}
	});
	
	$("#login_email").focus();
});