package org.displaytag.sample;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import org.apache.commons.lang.StringUtils;

/**
 * Just a test class that returns columns of data that are useful for testing
 * out the ListTag class and ListColumn class.
 *
 * @author epesh
 * @version $Revision$ ($Author$)
 */
public class ListObject extends Object
{
    /**
     * random number generator
     */
    private static Random random = new Random();

    /**
     * id
     */
    private int id = -1;

    /**
     * name
     */
    private String name;

    /**
     * email
     */
    private String email;

    /**
     * date
     */
    private Date date;

    /**
     * money
     */
    private double money;

    /**
     * description
     */
    private String description;

    /**
     * long description
     */
    private String longDescription;

    /**
     * status
     */
    private String status;

    /**
     * url
     */
    private String url;

    /**
     * sub list used to test nested tables
     */
    private ArrayList subList;

    /**
     * Constructor for ListObject
     */
    public ListObject()
    {
        this.id = random.nextInt(99998) + 1;
        this.money = (random.nextInt(999998) + 1) / 100;

        String firstName = RandomSampleUtil.getRandomWord();
        String lastName = RandomSampleUtil.getRandomWord();

        this.name = StringUtils.capitalise(firstName) + " " + StringUtils.capitalise(lastName);

        this.email = firstName + "-" + lastName + "@" + RandomSampleUtil.getRandomWord() + ".com";

        this.date = RandomSampleUtil.getRandomDate();

        this.description = RandomSampleUtil.getRandomWord() + " " + RandomSampleUtil.getRandomWord() + "...";

        this.longDescription = RandomSampleUtil.getRandomSentence(10);

        this.status = RandomSampleUtil.getRandomWord().toUpperCase();

        // added sublist for testing of nested tables
        this.subList = new ArrayList();
        this.subList.add(new SubListItem());
        this.subList.add(new SubListItem());
        this.subList.add(new SubListItem());

        this.url = "http://www." + lastName + ".org/";
    }

    /**
     * getter for id
     * @return int id
     */
    public int getId()
    {
        return this.id;
    }

    /**
     * setter for id
     * @param value int id
     */
    public void setId(int value)
    {
        this.id = value;
    }

    /**
     * getter for name
     * @return String name
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * getter for email
     * @return String email
     */
    public String getEmail()
    {
        return this.email;
    }

    /**
     * setter for email
     * @param value String email
     */
    public void setEmail(String value)
    {
        this.email = value;
    }

    /**
     * getter for date
     * @return Date
     */
    public Date getDate()
    {
        return this.date;
    }

    /**
     * getter for money
     * @return double money
     */
    public double getMoney()
    {
        return this.money;
    }

    /**
     * getter for description
     * @return String description
     */
    public String getDescription()
    {
        return this.description;
    }

    /**
     * getter for long description
     * @return String long description
     */
    public String getLongDescription()
    {
        return this.longDescription;
    }

    /**
     * getter for status
     * @return String status
     */
    public String getStatus()
    {
        return this.status;
    }

    /**
     * getter for url
     * @return String url
     */
    public String getUrl()
    {
        return this.url;
    }

    /**
     * test for null values
     * @return null
     */
    public String getNullValue()
    {
        return null;
    }

    /**
     * return a simple string representation of the object
     * @return String simple representation of the object
     */
    public String toString()
    {
        return "ListObject(" + this.id + ")";
    }

    /**
     * returns a detailed string representation of the object
     * @return String detailed representation of the object
     */
    public String toDetailedString()
    {
        return "ID:          "
            + this.id
            + "\n"
            + "Name:        "
            + this.name
            + "\n"
            + "Email:       "
            + this.email
            + "\n"
            + "Date:        "
            + this.date
            + "\n"
            + "Money:       "
            + this.money
            + "\n"
            + "Description: "
            + this.description
            + "\n"
            + "Status:      "
            + this.status
            + "\n"
            + "URL:         "
            + this.url
            + "\n";
    }

    /**
     * Returns the subList.
     * @return ArrayList
     */
    public ArrayList getSubList()
    {
        return this.subList;
    }

    /**
     * Inner class used in testing nested tables
     * @author fgiust
     */
    public class SubListItem
    {

        /**
         * name
         */
        private String name;

        /**
         * email
         */
        private String email;

        /**
         * Constructor for SubListItem
         */
        public SubListItem()
        {
            this.name = RandomSampleUtil.getRandomWord();
            this.email = RandomSampleUtil.getRandomEmail();
        }

        /**
         * getter for name.
         * @return String name
         */
        public String getName()
        {
            return this.name;
        }

        /**
         * getter for email.
         * @return String
         */
        public String getEmail()
        {
            return email;
        }

    }

}