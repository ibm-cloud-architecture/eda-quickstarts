package ibm.eda.demo.kafka.producer;

public class Order {
    public Integer trans_num;
    public String trans_dt;
    public String store_num;
    public String upc;
    public Integer quantity;
    public Double unit_price;
    public Boolean refund_ind;

    public Order() {}
}
