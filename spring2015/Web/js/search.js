function searchFunction(){
	var xmlHttp = new XMLHttpRequest();
	var url = "";
	
	xmlHttp.onreadystatechange = function(){
		if((xmlHttp.readystate == 4) && (xmlHttp.status == 200)){
			var result = JSON.parse(xmlHttp.response);
			displayResult(result);
		}
	}
}	
function displayResult(arr){
	
}