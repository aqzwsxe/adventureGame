package AdventureModel;

import java.util.Random;

public class Weather {
    Random random = new Random();

    public String today_weather (){
        final int i = random.nextInt(10);
        String weather;
        switch (i){
            case 1:
                weather = "Sunny: 6°C ~ 10°C";
                break;
            case 2:
                weather = "Rainy: 6°C ~ 10°C";
                break;
            case 3:
                weather = "Rainy: 6°C ~ 10°C";
                break;
            case 4:
                weather = "Cloudy: 5°C ~ 11°C";
                break;
            case 5:
                weather = "snowy: -10°C ~ 1°C";
                break;

            default:
                weather = "foggy: -1°C ~ 15°C";
                
        }
        return weather;

    }
}
