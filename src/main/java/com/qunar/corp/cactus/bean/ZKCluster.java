package com.qunar.corp.cactus.bean;

/**
 * Created by IntelliJ IDEA.
 * User: liuzz
 * Date: 13-10-28
 * Time: 下午6:56
 */
public class ZKCluster {

    private long id;

    private String address;

    private String name;

    //这个构造方法必须要有，mybatis查询数据库后装配到此对象时，需要先调用这个方法来创建对象
    public ZKCluster() {
    }

    public ZKCluster(ZKCluster other) {
        this.id = other.id;
        this.name = other.name;
        this.address = other.address;
    }

    public ZKCluster(String name, String address) {
        this.name = name;
        this.address = address;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ZKCluster)) return false;

        ZKCluster other = (ZKCluster) o;

        if (id != other.id) return false;
        if (address != null ? !address.equals(other.address) : other.address != null) return false;
        if (name != null ? !name.equals(other.name) : other.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (address != null ? address.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ZKCluster{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}
