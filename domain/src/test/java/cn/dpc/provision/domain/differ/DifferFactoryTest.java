package cn.dpc.provision.domain.differ;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class DifferFactoryTest {

    @Mock
    private FullDiffer fullDiffer;

    @Mock
    private IncrementalDiffer incrementalDiffer;

    private DifferFactory differFactory;

    @BeforeEach
    public void setUp() {
        differFactory  = new DifferFactory(fullDiffer, incrementalDiffer);
    }
    
    @Test
    void getByType() {
        Assertions.assertEquals(differFactory.getByType("FULL_TEST"), fullDiffer);
        Assertions.assertEquals(differFactory.getByType("INCR_TEST"), incrementalDiffer);
        Assertions.assertEquals(differFactory.getByType("NOT_EXIST"), null);

    }
}