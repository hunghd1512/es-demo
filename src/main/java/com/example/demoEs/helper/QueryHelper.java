package com.example.demoEs.helper;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.JsonData;
import com.sun.jdi.InternalException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class QueryHelper {

    private final ElasticsearchClient elasticsearchClient;

    void updatePrevent(String query,String indexStr,String indexId) {
        try {
            String scriptSource = """
                        ctx._source.dataPreventES.updateCount =\s
                            ctx._source.dataPreventES.containsKey('updateCount') && ctx._source.dataPreventES.updateCount != null\s
                                ? ctx._source.dataPreventES.updateCount + 1\s
                                : 1;
                                       \s
                     Map typeToField = [
                              'MOTORBIKE_VH': 'motorbikeVehicleES',
                              'CAR_VH': 'carVehicleES',
                              'BOAT_VH': 'boatVehicleES',
                              'FISHING_SHIP_VH': 'fishingShipVehicleES',
                              'AUTHORITY_USE_LAND_RS': 'authorityUseLandResourcesES',
                              'REAL_ESTATE_OTHER': 'realEstateOtherES',
                              'BONDS_PP': 'bondsPaperES',
                              'STOCK_PP': 'stockPaperES',
                              'SAVING_BOOK_PP': 'savingBookPaperES',
                              'AUTHORITY_USE_LAND_AND_PROPERTY_ASSOCIATE_LAND_RS': 'landUseAndAttachedPropertiesES',\s
                              'AUTHORITY_FROM_PROPERTY_SALES_CONTRACT_FORMED_FUTURE_RS': 'realEstateContractRightsESListES',
                              'AIR_SHIP_VH': 'airShipVehicleES',
                              'SEA_SHIP_VH': 'seaShipVehicleES',
                              'OTHER': 'propertyOtherES',
                              'PERSONAL': 'personalsES',
                              'ORGANIZATION': 'organizationsES'
                          ];
                                       \s
                        for (target in params.targets) {
                            String type = target.type;
                            String uuid = target.uuid;
                            String field = typeToField.get(type);
                                       \s
                            if (field != null && ctx._source.containsKey(field)) {
                                for (item in ctx._source[field]) {
                                    if (item.uuid == uuid) {
                                        if (item.containsKey('release_status')) {
                                            item.release_status = params.newReleaseStatus;
                                        }
                                        if (item.containsKey('releaseStatus')) {
                                            item.releaseStatus = params.newReleaseStatus;
                                        }
                                    }
                                }
                            }
                        }
                   \s""";
            //
            List<Map<String, String>> targets = new ArrayList<>();
            // Create script parameters
            Map<String, JsonData> scriptParams = new HashMap<>();
            scriptParams.put("newReleaseStatus", JsonData.of("APPROVED"));
            scriptParams.put("targets", JsonData.of(targets));

            // Use the injected client directly
            elasticsearchClient.update(
                    u -> u.index("IndexName")
                            .id(indexId)
                            .script(
                                    s -> s.lang("painless")
                                            .source(scriptSource)
                                            .params(scriptParams)
                            ),
                    Void.class
            );
        } catch (Exception e) {
            throw new InternalException("Có lỗi khi update data prevent");
        }
    }
}
