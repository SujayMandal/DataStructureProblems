package com.ca.framework.core.db.listeners;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class EntityEncryptionListenerTest {
    
    @Test
    public void testDecryptAndDecrypt() {
        EntityForEncryption entity = new EntityForEncryption();
        entity.setSsn("23445555asdf987kjnjkh");
        EntityEncryptionListener classUnderTest = new EntityEncryptionListener();
        //classUnderTest.init();
        classUnderTest.setKey("password");
        
        classUnderTest.encrypt(entity);
        assertThat(entity.getSsn(), not("23445555asdf987kjnjkh"));
        
        classUnderTest.decrypt(entity);
        assertThat(entity.getSsn(), is("23445555asdf987kjnjkh"));
    }

}
