package org.apache.solr.handler.component;

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.solr.common.params.FacetParams;
import org.apache.solr.common.params.SolrParams;

/**
 * Created by harshvardhan.s on 27/11/15.
 */
public class RestrictiveFacetComponent extends FacetComponent implements RestrictiveSearchComponent {

    @Override
    public Boolean validate(SolrParams request, RestrictiveComponentAttributes restrictiveComponentAttributes) throws RestrictedQueryException {

        if(request.get(FacetParams.FACET_SORT) != null) {
            throw new RestrictedQueryException("Facet Sorting not allowed.");
        }

        if (matchPattern("([0-9]+)", request.get(FacetParams.FACET_LIMIT,""))) {
            throw new RestrictedQueryException("Facet queries cannot be used without facet.limit or having facet.limit to -1.");
        }

        if (matchPattern("((%2D|%2B|\\+|-)*([3-9][0-9]SECOND|[0-9]{1,2}(MINUTE|HOUR)))", request.get(FacetParams.FACET_RANGE_GAP,""))) {
            throw new RestrictedQueryException("Facet range gap has to be greater than 30 SECOND and you cannot have decimal value for gap.");
        }

        String fieldName = request.get(FacetParams.FACET_FIELD);

        if(fieldName != null && checkCardinality(request, restrictiveComponentAttributes,fieldName) == false ){
            throw new RestrictedQueryException("Facet on high cardinality fields are not allowed.");
        }



        return true;
    }
}
