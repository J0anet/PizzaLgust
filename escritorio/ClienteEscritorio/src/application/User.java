package application;

public class User {
	private String id;
    private String userName;
    private String email;
    private String firstName;
    private String lastName;
    private boolean isAdmin;
    private String userType;
    

	public User() {
		super();
	}
	
	public User(String id, String userName, String email, String firstName, String lastName, boolean isAdmin,
			String userType) {
		super();
		this.id = id;
		this.userName = userName;
		this.email = email;
		this.firstName = firstName;
		this.lastName = lastName;
		this.isAdmin = isAdmin;
		this.userType = userType;
	}

	@Override
	public String toString() {
		return "id: " + id + ", usuari: " + userName + ", email: " + email + ", nom: " + firstName
				+ ", cognom: " + lastName + ", isAdmin: " + isAdmin + ", tipus: " + userType;
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public boolean isAdmin() {
		return isAdmin;
	}
	public void setAdmin(boolean isAdmin) {
		this.isAdmin = isAdmin;
	}
	public String getUserType() {
		return userType;
	}
	public void setUserType(String userType) {
		this.userType = userType;
	}
}
