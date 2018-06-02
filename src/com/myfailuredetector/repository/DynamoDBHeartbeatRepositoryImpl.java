package com.myfailuredetector.repository;

import java.util.HashMap;
import java.util.Map;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.myfailuredetector.data.Constants;
import com.myfailuredetector.data.Heartbeat;

public class DynamoDBHeartbeatRepositoryImpl<T> implements HeartbeatRepository<Heartbeat>
{
    private final AmazonDynamoDB dynamoDB;

    public DynamoDBHeartbeatRepositoryImpl( final Regions region )
    {
        this.dynamoDB = AmazonDynamoDBClientBuilder.standard()
                                                   .withRegion( region )
                                                   .build();
    }

    @Override
    public void store( final Heartbeat data )
    {
        this.dynamoDB.putItem( Constants.HEARTBEAT_TABLE, this.convertToAttributeMap(data) );
    }

    /*
    {
      "hostState": "HEALTHY",
      "hostUUID": "host-1527905197",
      "serviceName": "MyAwesomeService",
      "timestamp": 1527905197
    }
     */
    private Map<String, AttributeValue> convertToAttributeMap( final Heartbeat heartbeat )
    {
        Map<String, AttributeValue> attributeValueMap = new HashMap<>(  );
        attributeValueMap.put( "hostState", new AttributeValue( heartbeat.getHostState().toString()) );
        attributeValueMap.put( "hostUUID", new AttributeValue( heartbeat.getHostUUID() ) );
        attributeValueMap.put( "serviceName", new AttributeValue( heartbeat.getServiceName() ) );
        attributeValueMap.put( "timestamp", longToAttributeValue( heartbeat.getTimestamp().getTime() ) );
        return attributeValueMap;
    }

    private AttributeValue longToAttributeValue( final long time )
    {
        AttributeValue attr = new AttributeValue( );
        attr.setN( String.valueOf( time ));
        return attr;
    }
}
