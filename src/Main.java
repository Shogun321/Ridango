import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class Main
{
    public static void main(String[] args)
    {
        Scanner myObj = new Scanner(System.in);
        String line;
        String splitBy = ",";
        int DesiredStop=2;
        int number_of_buses=5;
        String mode="absolute";
        String stop_name;
        //busTrips 2 5 absolute

        while (true) {
            try{
                System.out.println("Enter command:");
                String[] input = myObj.nextLine().split(" ");  // Read user input
                DesiredStop=Integer.parseInt(input[1]);
                number_of_buses=Integer.parseInt(input[2]);
                mode=input[3];
                stop_name= get_stop_name(String.valueOf(DesiredStop));
                if(stop_name==null){
                    System.out.println("Invalid input.");
                    continue;
                }
                break;
            }catch (Exception e) {
                myObj.nextLine(); // clear the input buffer
                System.out.println("Invalid input.");
            }
        }
        myObj.close();

        try
        {
            Map<String, List<String>> arrival_times= new HashMap<>();
            BufferedReader br = new BufferedReader(new FileReader("src/stop_times.txt"));
            while ((line = br.readLine()) != null)
            {
                String[] stop_times = line.split(splitBy);    // use comma as separator
                boolean isDigits = stop_times[3].matches("\\d+");
                String[] arrival_information;
                if(isDigits)
                if(Integer.parseInt(stop_times[3])== DesiredStop && compute_time_difference(stop_times[1])){//find the desired stop
                    arrival_information=stop_times[0].split("_");
                    if (arrival_times.containsKey(arrival_information[2])) {
                        if(arrival_times.get(arrival_information[2]).size()<number_of_buses) {
                            if (mode.matches("absolute"))
                                arrival_times.get(arrival_information[2]).add(stop_times[1]);
                            else
                                arrival_times.get(arrival_information[2]).add(toRelativeTime(stop_times[1]));
                        }
                    } else {
                        List<String> values = new ArrayList<>();
                        if(mode.matches("absolute"))
                            values.add(stop_times[1]);
                        else
                            values.add(toRelativeTime(stop_times[1]));
                        arrival_times.put(arrival_information[2], values);
                    }
                }
            }
            System.out.println("Postajališče "+stop_name+":");
            for (String key:arrival_times.keySet()) {
                System.out.println(key+": "+arrival_times.get(key));
            }
            br.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    static boolean compute_time_difference(String time1){
        LocalTime currentTime = LocalTime.now();
        LocalTime otherTime = LocalTime.parse(time1);
        // make sure the other time is not earlier than the current time
        if (otherTime.isBefore(currentTime)) {
            return false;
        }
        long diffHours = ChronoUnit.HOURS.between(currentTime, otherTime);
        if (diffHours <= 2) return true;
        return false;
    }
    static String toRelativeTime(String time1){
        LocalTime currentTime = LocalTime.now();
        LocalTime otherTime = LocalTime.parse(time1);
        Duration duration = Duration.between(currentTime, otherTime);
        return Long.toString(duration.toMinutes());
    }
    static String get_stop_name(String stop_id){
        String line;
        try{
            BufferedReader br = new BufferedReader(new FileReader("src/stops.txt"));
            while ((line = br.readLine()) != null){
                String[] stops = line.split(",");
                if(stop_id.matches(stops[0])){
                    return stops[2];
                }
            }
            br.close();
        }catch (IOException e)
        {
            return null;
        }
        return null;
    }
}  