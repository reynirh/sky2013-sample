

function updateWeather( path){
    $.get(path, function(responseText) {
        document.getElementById('weatherdesc').innerHTML = ('<h4>Veðrið</h4>' + responseText);

    });

}
