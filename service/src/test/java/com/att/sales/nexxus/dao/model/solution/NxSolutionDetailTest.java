package com.att.sales.nexxus.dao.model.solution;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.att.sales.nexxus.dao.model.NxRequestDetails;
@ExtendWith(MockitoExtension.class)
public class NxSolutionDetailTest {

	@InjectMocks
	NxSolutionDetail test;

	@Mock
	private NxRequestDetails nxRequestDetail;

	@Mock
	private NxTeam nxTeam;

	@Test
	public void test() {
		test.removeNxRequestDetail(nxRequestDetail);
		Set<NxTeam> nxTeams = new HashSet<>();
		nxTeams.add(nxTeam);
		test.setNxTeams(nxTeams );
		test.removeNxTeam(nxTeam);
		test.addNxTeam(nxTeam);
	}

}
