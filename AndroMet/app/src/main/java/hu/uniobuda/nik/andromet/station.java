package hu.uniobuda.nik.andromet;

public class station {

    // egy adott állomás adatai külön osztálypéldányt képeznek

    private int ID; // adatbázis szerinti ID
    private String location; // mérőállomás telepített helyszíne

    public station(int ID, String location) {
        this.ID = ID;
        this.location = location;
    }

    public int getID() {
        return ID;
    }

    public String getLocation() {
        return location;
    }

    @Override
    public String toString()
    {
        return location;
    }

}
