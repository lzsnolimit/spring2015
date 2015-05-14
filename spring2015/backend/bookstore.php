<?php
// +----------------------------------------------------------------------+
// | PHP version 5                                                        |
// +----------------------------------------------------------------------+
// | Copyright (c) 1997-2004 The PHP Group                                |
// +----------------------------------------------------------------------+
// | This source file is subject to version 3.0 of the PHP license,       |
// | that is bundled with this package in the file LICENSE, and is        |
// | available through the world-wide-web at the following url:           |
// | http://www.php.net/license/3_0.txt.                                  |
// | If you did not receive a copy of the PHP license and are unable to   |
// | obtain it through the world-wide-web, please send a note to          |
// | license@php.net so we can mail you a copy immediately.               |
// +----------------------------------------------------------------------+
// | Authors: Zhongshan Lu <lzsnolimit@gmail.com>                         |
// |          							                                  |
// +----------------------------------------------------------------------+
//
// $Id:$

include_once ("snoopy.php");
try {
	$courses=array();
    $con = mysql_connect("localhost", "root", "");
    if (!$con) {
        die('Could not connect: ' . mysql_error());
    }
    mysql_select_db('myuwyo', $con) or die('Can\'t use myuwyo : ' . mysql_error());
    $snoopy = new Snoopy;
    set_time_limit(0);
    $currentPoint = 394268;
    $url = "http://wyoming.verbacompare.com/comparison?id=";
    while ($currentPoint < 400000) {
        $newUrl = $url . $currentPoint;
        $snoopy->fetch($newUrl); //获取所有内容
        $str = $snoopy->results;
        $start = strpos($str, "[{");
        $stop = strpos($str, "]}]");
        if ($start !== false && $stop !== false) {
            //echo substr($str,$start,$stop-$start+3);
            $json = substr($str, $start, $stop - $start + 3);
            //var_dump(json_decode($json));
            $jsonArray = json_decode($json, true);
            $jsonArray = $jsonArray[0];
            //echo $jsonArray["id"];
            $title = $jsonArray["title"];
            $re1 = '((?:[a-z][a-z]+))'; // Word 1
            $re2 = '( )'; // White Space 1
            $re3 = '(\\d+)'; // Integer Number 1
            if ($c = preg_match_all("/" . $re1 . $re2 . $re3 . "/is", $title, $matches)) {
                $courseDepart = $matches[1][0];
                $courseId = $matches[3][0];
            }
            $sql = "SELECT * FROM `textbook` WHERE `id` = " . $jsonArray["id"];
            // echo $sql;
            mysql_query($sql);
            if (mysql_affected_rows() == 0) {
                //echo mysql_affected_rows();
                $sql = "INSERT INTO `myuwyo`.`textbook` (`id`, `booksJson`, `instructure`, `courseDepartment`, `courseId`, `courseSection`, `compareJson`) VALUES ('" . $jsonArray["id"] . "', '" . addslashes(json_encode($jsonArray["books"])) . "', '" . $jsonArray["instructor"] . "', '" . $courseDepart . "', '" . $courseId . "', '" . $jsonArray["name"] . "', '" . addslashes(json_encode($jsonArray["books"])) . "');";
                mysql_query($sql);
				if(!isset($courses[$courseDepart]))
				{
					$courses[$courseDepart]=array();
					//array_push($courses[$courseDepart],);
					}
				if(!isset($courses[$courseDepart][$courseId]))
				{
					$courses[$courseDepart][$courseId]=array();
				}
				//array_push($courses[$courseDepart][$courseId],$jsonArray["name"]);
				$courses[$courseDepart][$courseId][$jsonArray["name"]]=$jsonArray["id"];
			}
        } else {
        }
        $currentPoint++;
    }
    //echo "got it";
	$sql="INSERT INTO `myuwyo`.`course` (`json`) VALUES ('".addslashes(json_encode($courses))."')";
	mysql_query($sql);
	//print_r($courses);
    mysql_close($con);
}
catch(Exception $e) {
    mysql_close($con);
    echo 'Caught exception: ', $e->getMessage() , "\n";
}
?>

