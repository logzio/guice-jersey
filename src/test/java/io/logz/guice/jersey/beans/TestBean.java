package io.logz.guice.jersey.beans;

import java.util.Date;

public class TestBean {

    private int id;
    private String name;
    private Date created;

    public TestBean() {}

    public TestBean(int id, String name, Date created) {
        this.id = id;
        this.name = name;
        this.created = created;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TestBean testBean = (TestBean) o;

        return id == testBean.id && name.equals(testBean.name) && created.equals(testBean.created);
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + name.hashCode();
        result = 31 * result + created.hashCode();
        return result;
    }

}
