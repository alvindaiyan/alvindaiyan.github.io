---
layout: post
title:  "Tutorial: MyBatis using Java"
date:   2015-03-04 11:21:00
categories: java
---

[MyBatis](https://mybatis.github.io/mybatis-3/) is a widely used light-weight ORM tool. MyBatis can use XML or Java Annotations for configure all Jdbc, sql mapping and [POJOs](http://en.wikipedia.org/wiki/Plain_Old_Java_Object) to database.

![mybatis]({{ site.url }}/assets/mybatis-logo.png)

In this tutorial, I will create a simple program to demonstrate how to setup a java project with MyBatis.


## Create a MyBaits project

The tools used:

- [jdk 8.0](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
- [postgres 9.3](http://www.postgresql.org/download/)
- [Maven 3](http://maven.apache.org/download.cgi)
- [mybatis 3.2.8](https://mybatis.github.io/mybatis-3/)

<ol>
<li> Create database:
{% highlight sql%}
CREATE TABLE userinfo
(
  uid serial NOT NULL,
  username character varying(100) NOT NULL,
  password character varying(500) NOT NULL,
  created date,
  CONSTRAINT user_pkey PRIMARY KEY (uid),
  CONSTRAINT unq_username UNIQUE (username)
)
{% endhighlight %}
</li>
<li>
Create a maven project and setup the dependencies:
{% highlight xml %}
...
 	<dependency>
            <groupId>org.mybatis</groupId>
            <artifactId>mybatis</artifactId>
            <version>3.2.8</version>
        </dependency>

        <dependency>
            <groupId>postgresql</groupId>
            <artifactId>postgresql</artifactId>
            <version>9.1-901.jdbc4</version>
        </dependency>
...
{% endhighlight %}
</li>
<li>
Create the <b>jdbc.properties</b> in <b>main/resources</b>
{% highlight java %}
jdbc.driverClassName=org.postgresql.Driver
jdbc.url=jdbc:postgresql://your_db_hosting_address/your_db_name
jdbc.username=yourusername
jdbc.password=userpassword
{% endhighlight%}
</li>

<li>
Setup the mybatis configuration file <b>mybatis-config.xml</b> in <b>main/resources</b>
{% highlight xml %}
<?xml version='1.0' encoding='UTF-8' ?>
<!DOCTYPE configuration
        PUBLIC '-//mybatis.org//DTD Config 3.0//EN'
        'http://mybatis.org/dtd/mybatis-3-config.dtd'>
<configuration>
    <properties resource='jdbc.properties'/>
    <typeAliases>
        <typeAlias type='com.learnBatis.UserInfo' alias='User'></typeAlias>
    </typeAliases>
    <environments default='development'>
        <environment id='development'>
            <transactionManager type='JDBC'/>
            <dataSource type='POOLED'>
                <property name='driver' value='${jdbc.driverClassName}'/>
                <property name='url' value='${jdbc.url}'/>
                <property name='username' value='${jdbc.username}'/>
                <property name='password' value='${jdbc.password}'/>
            </dataSource>
        </environment>
    </environments>
    <mappers>
        <mapper resource='mappers/UserMapper.xml'/>
    </mappers>
</configuration>
{% endhighlight %}
</li>


<li>
Setup the <b>UserMapper.xml</b> in <b>main/resources/mappers</b> (You can use Java Annotation instead of XML as well)

{% highlight xml %}
<?xml version='1.0' encoding='UTF-8' ?>
<!DOCTYPE mapper PUBLIC '-//mybatis.org//DTD Mapper 3.0//EN'
        'http://mybatis.org/dtd/mybatis-3-mapper.dtd'>

<mapper namespace='com.learnBatis.UserMapper'>

    <select id='getUserById' parameterType='int' resultType='com.learnBatis.UserInfo'>
        SELECT uid, username, password, created from userinfo where uid = #{userid}
    </select>
    <resultMap type='User' id='UserResult'>
        <id property='uid' column='uid'/>
        <result property='username' column='username'/>
        <result property='password' column='password'/>
        <result property='created' column='created'/>
    </resultMap>

    <select id='getAllUsers' resultMap='UserResult'>
        SELECT * FROM USERINFO
    </select>

    <insert id='insertUser' parameterType='User' useGeneratedKeys='true' keyProperty='userId'>
        INSERT INTO USERINFO(uid, username, password, created)
        VALUES(#{uid}, #{username}, #{password}, #{created})
    </insert>

    <update id='updateUser' parameterType='User'>
        UPDATE USERINFO
        SET
        PASSWORD= #{password},
        USERNAME = #{username}
        CREATED = #{created},
        WHERE UID = #{uid}
    </update>

    <delete id='deleteUser' parameterType='int'>
        DELETE FROM USERINFO WHERE UID = #{userId}
    </delete>

</mapper>
{% endhighlight %}
</li>

<li> 
Create the model Object <b>UserInfo.class</b> in <b>main/java/com/learnBatis</b>
{% highlight java %}
package com.learnBatis;

public class UserInfo
{
    private int uid;
    private String username;
    private String password;
    private String created;

    @Override
    public String toString()
    {
        return "user name: " + username + ", pwd: " + password + ", Created: " + created;
    }
}
{% endhighlight %}
</li>

<li>
Create the mapper interface <b>UserMapper.interface</b> in main/java/com/learnBatis

{% highlight java %}
package com.learnBatis;

import java.util.List;

public interface UserMapper
{
    public void insertUser(UserInfo user);
    public UserInfo getUserById(Integer userid);
    public List<UserInfo> getAllUsers();
    public void updateUser(UserInfo user);
    public void deleteUser(Integer userid);
}
{% endhighlight %}
</li>

<li>
Now, we need setup the connection to the db by creating the <b>MyBatisUtil.class</b> in <b>com.learnBatis</b> package 	
{% highlight java %}
package com.learnBatis;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.Reader;

public class MyBatisUtil
{
    private static SqlSessionFactory factory;

    private MyBatisUtil() {
    }

    static
    {
        Reader reader = null;
        try {
            reader = Resources.getResourceAsReader("mybatis-config.xml");
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
        factory = new SqlSessionFactoryBuilder().build(reader);
    }

    public static SqlSessionFactory getSqlSessionFactory()
    {
        return factory;
    }

}
{% endhighlight %}
</li>
<li>
We need use the mapper to query the db, so we create a <b>UserService.class</b> in <b>com.learnBatis</b>
{% highlight java %}
package com.learnBatis;

import org.apache.ibatis.session.SqlSession;

import java.util.List;

public class UserService
{

    public void insertUser(UserInfo user)
    {
        SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession();
        try
        {
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            userMapper.insertUser(user);

            sqlSession.commit();
        }
        finally
        {
            sqlSession.close();
        }
    }

    public UserInfo getUserById(Integer userid)
    {
    	...
    }

    public List<UserInfo> getAllUsers()
    {
        SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession();
        try
        {
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);

            return userMapper.getAllUsers();
        }
        finally
        {
            sqlSession.close();
        }
    }

    public void updateUser(UserInfo user)
    {
    	...
    }

    public void deleteUser(Integer userid)
    {
    	...
    }
}

{% endhighlight %}
</li>
</ol>


## Use MyBatis SQLMapper

Here, we has completed all the tasks needed for setting up MyBatis to our psql database. To test our program, we can simply write a <b>Main.class</b>:

{% highlight java %}
package com.learnBatis;

import java.util.List;

public class Main
{
    public static void main(String[] args)
    {
        UserService userService = new UserService();

        List<UserInfo> users = userService.getAllUsers();
        for (UserInfo u : users)
        {
            System.out.println(u.toString());
        }
    }
}
{% endhighlight %}

The output will be like: 
{% highlight java %}
user name: test0, pwd: testpwd, Created: 2015-02-09
user name: test1, pwd: testpwd2, Created: 2015-02-10
{% endhighlight %}