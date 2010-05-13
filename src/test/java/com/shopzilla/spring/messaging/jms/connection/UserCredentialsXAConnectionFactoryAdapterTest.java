package com.shopzilla.spring.messaging.jms.connection;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.jms.*;


/**
 * Unit tests for {@link com.shopzilla.spring.messaging.jms.connection.UserCredentialsXAConnectionFactoryAdapter}
 *
 * @author Josh Long
 */
public class UserCredentialsXAConnectionFactoryAdapterTest {
    private Mockery context = new Mockery() {

        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    private String user = "user";
    private String pw = "pw";
    private UserCredentialsXAConnectionFactoryAdapter userCredentialsXAConnectionFactoryAdapter;
    private TargetConnectionFactory xaConnectionFactory;
    private ConnectionFactory connectionFactory;

    @Before
    public void before() throws Throwable {
        this.connectionFactory = this.context.mock(ConnectionFactory.class);
        this.xaConnectionFactory = this.context.mock(TargetConnectionFactory.class);
        this.userCredentialsXAConnectionFactoryAdapter = new UserCredentialsXAConnectionFactoryAdapter();
        this.userCredentialsXAConnectionFactoryAdapter.setPassword(pw);
        this.userCredentialsXAConnectionFactoryAdapter.setUsername(user);
        this.userCredentialsXAConnectionFactoryAdapter.setTargetConnectionFactory(this.xaConnectionFactory);
    }

    @After
    public void after() throws Throwable {
        this.context.assertIsSatisfied();
    }

    @Test(expected = RuntimeException.class)
    public void testSettingTargetConnectionFactory() throws Throwable {
        this.userCredentialsXAConnectionFactoryAdapter.setTargetConnectionFactory(this.connectionFactory);
    }

    @Test
    public void testCreateConnectionWithUserAndPassword()
        throws Throwable {
        this.context.checking(new Expectations() {
            {
                one(xaConnectionFactory).createXAConnection(user, pw);
            }
        });
        this.userCredentialsXAConnectionFactoryAdapter.createXAConnection(user, pw);
    }

    @Test
    public void testCreateConnection() throws Throwable {
        this.context.checking(new Expectations() {
            {
                one(xaConnectionFactory).createXAConnection(user, pw);
            }
        });
        this.userCredentialsXAConnectionFactoryAdapter.createXAConnection();
    }
}


/**
 * There doesn't seem to be a readily accessed way to mock multiple interaces using JMock , so this stub will do.
 */
class TargetConnectionFactory implements XAConnectionFactory, ConnectionFactory {
    public TargetConnectionFactory() {
        super(); //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public int hashCode() {
        return super.hashCode(); //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o); //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone(); //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public String toString() {
        return super.toString(); //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize(); //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public Connection createConnection() throws JMSException {
        return null; //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Connection createConnection(String s, String s1)
        throws JMSException {
        return null; //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public XAConnection createXAConnection() throws JMSException {
        return null; //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public XAConnection createXAConnection(String s, String s1)
        throws JMSException {
        return null; //To change body of implemented methods use File | Settings | File Templates.
    }
}
