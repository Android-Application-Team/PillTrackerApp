<?php

include 'config.php';

$mysqli = new mysqli($host,$username,$password,$dbname);

// check connection
if ($mysqli->connect_errno) {
    printf("Connection failed: %s\n", $mysqli->connect_error);
    exit();
}

//hello
// get Json data
$json_obj = json_decode(file_get_contents("php://input"), true);
$username = $json_obj["userName"];
$password = $json_obj["password"];



// checkinng username
$query = $mysqli->prepare('SELECT * FROM profile WHERE username = ?'); // SQL Injection prevention
$query->bind_param('s', $username); // 's' specifies the variable type => 'string'
$query->execute();
$result = $query->get_result();
$rows = $result->num_rows;

if ($rows == 0){

    $status = array('status' => 'false');
    $jsonStatus= json_encode($status);
    echo $jsonStatus;

    $mysqli->close();
}

else if ($rows == 1){

    //checking password
    $password_query="SELECT password FROM profile WHERE username = '$username'";
    $fetch_password = $mysqli->query($password_query);
    $row_info1 = $fetch_password->fetch_row();
    $user_password = $row_info1[0];
    $password_hash = sha1($password);



    if($user_password != $password_hash){
        $status = array('status' => 'false');
        $jsonStatus= json_encode($status);
        echo $jsonStatus;

    }

    else {
        $status = array('status' => 'true');
        $jsonStatus= json_encode($status);
//        $myfile = fopen("output2.txt", "w") or die("Unable to open file!");
//        fwrite($myfile, $jsonStatus);
//        fclose($myfile);
        echo $jsonStatus;

    }

    $mysqli->close();

}

//else{
//    echo "Error";//Display message if the query did not
//    // produce a result
//    $mysqli->close();
//
//}

?>