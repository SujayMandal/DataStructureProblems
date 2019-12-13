package com.fa.dp.business.search.delegate;

import com.fa.dp.business.search.info.FutureReductionSearchDetails;
import com.fa.dp.core.exception.SystemException;

import java.util.concurrent.ExecutionException;

public interface SearchDelegate {

    public FutureReductionSearchDetails getFutureReductionDetails(String assetNumber, String oldAssetNumber, String propTemp, String occupancy) throws
            SystemException, ExecutionException, InterruptedException;
}
