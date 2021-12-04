package ibm.eda.demo.ordermgr.infra.events;

public class Address {

  /** Street name with number within the street */
   private java.lang.String street;
  /** city */
   private java.lang.String city;
  /** State code or name */
   private java.lang.String state;
  /** Country */
   private java.lang.String country;
  /** Zipcode */
   private java.lang.String zipcode;

  /**
   * Default constructor.  Note that this does not initialize fields
   * to their default values from the schema.  If that is desired then
   * one should use <code>newBuilder()</code>.
   */
  public Address() {}

  /**
   * All-args constructor.
   * @param street Street name with number within the street
   * @param city city
   * @param state State code or name
   * @param country Country
   * @param zipcode Zipcode
   */
  public Address(java.lang.String street, java.lang.String city, java.lang.String state, java.lang.String country, java.lang.String zipcode) {
    this.street = street;
    this.city = city;
    this.state = state;
    this.country = country;
    this.zipcode = zipcode;
  }

  public java.lang.String getStreet() {
    return street;
  }

  public java.lang.String getCity() {
    return city;
  }

  public java.lang.String getState() {
    return state;
  }


  public java.lang.String getCountry() {
    return country;
  }


  public java.lang.String getZipcode() {
    return zipcode;
  }

}










