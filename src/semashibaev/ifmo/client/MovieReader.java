package semashibaev.ifmo.client;


import java.util.ArrayList;


public class MovieReader {
    public static ArrayList<String> readMovie(ArrayList<String> form) throws Exception {
        ArrayList<String> movieArgs = new ArrayList<>();

        if (form.get(0) == null || form.get(0).isEmpty())
                throw new Exception("**Invalid name");
            movieArgs.add(form.get(0));


            Integer oscar = Integer.parseInt(form.get(1));
            if (oscar <= 0)
                throw new Exception("**Invalid oscarsCount (must be > 0)");
            movieArgs.add(form.get(1));


            Integer palm = Integer.parseInt(form.get(2));
            if (palm <= 0)
                throw new Exception("**Invalid goldenPalmCount (must be > 0)");
            movieArgs.add(form.get(2));


            if (form.get(3).length() > 15)
                throw new Exception("**Invalid totalBoxOffice(too many too many numbers) ");
            Double box = Double.parseDouble(form.get(3));
            if (box <= 0)
                throw new Exception("**Invalid totalBoxOffice (must be > 0)");
            movieArgs.add(form.get(3));

            Long coordX = Long.parseLong(form.get(4));
            if (coordX <= -112)
                throw new Exception("**Invalid x coord (must be > -112)");
            movieArgs.add(form.get(4));

            Long coordY = Long.parseLong(form.get(5));
            if (coordY <= -259)
                throw new Exception("**Invalid y coord (must be > -259)");
            movieArgs.add(form.get(5));

            if (form.get(6) == null || form.get(6).isEmpty())
                throw new Exception("**Invalid name");
            movieArgs.add(form.get(6));

            if (form.get(7).equals("R") || form.get(7).equals("G") || form.get(7).equals("PG") || form.get(7).equals("NC_17"))
                movieArgs.add(form.get(7));
            else {
                throw new Exception("**Invalid mpaa rating");
            }

        return movieArgs;
    }
}

