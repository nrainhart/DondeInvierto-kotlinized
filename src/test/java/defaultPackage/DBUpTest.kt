package defaultPackage

import org.junit.Test
import org.uqbarproject.jpa.java8.extras.WithGlobalEntityManager
import org.uqbarproject.jpa.java8.extras.test.AbstractPersistenceTest

import org.junit.Assert.assertNotNull

class DBUpTest : AbstractPersistenceTest(), WithGlobalEntityManager {

    @Test
    fun contextUp() {
        assertNotNull(entityManager())
    }

    @Test
    fun contextUpWithTransaction() {
        withTransaction { }
    }
}
