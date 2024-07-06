import java.util.ArrayList;
import java.util.List;

public class Company {
    private String id;
    private String name;
    private String address;
    private String phone;
    private List<String> sharedWith; // Liste des UID des utilisateurs avec qui l'entreprise est partagée

    public Company() {
        // Constructeur vide nécessaire pour Firebase
    }

    public Company(String id, String name, String address, String phone) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.sharedWith = new ArrayList<>();
    }

    // Getters et Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public List<String> getSharedWith() {
        return sharedWith;
    }

    public void setSharedWith(List<String> sharedWith) {
        this.sharedWith = sharedWith;
    }

    public void shareWith(String uid) {
        if (!sharedWith.contains(uid)) {
            sharedWith.add(uid);
        }
    }
}
