/**
 * Copyright (c) 2012 Partners In Health.  All rights reserved.
 * The use and distribution terms for this software are covered by the
 * Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
 * which can be found in the file epl-v10.html at the root of this distribution.
 * By using this software in any fashion, you are agreeing to be bound by
 * the terms of this license.
 * You must not remove this notice, or any other, from this software.
 **/
package org.pih.warehouse.api

import grails.converters.JSON
import org.apache.commons.collections.FactoryUtils
import org.apache.commons.collections.list.LazyList
import org.pih.warehouse.core.Location
import org.pih.warehouse.core.Person
import org.pih.warehouse.order.Order
import org.pih.warehouse.requisition.Requisition
import org.pih.warehouse.shipping.Shipment
import org.pih.warehouse.shipping.ShipmentItem

class StockMovementApiController {

    def stockMovementService

    def list = {
        def stockMovements = stockMovementService.getStockMovements(10)
        render ([stockMovements.collect { it.toJson() }] as JSON)
    }

    def read = {
        StockMovement stockMovement = stockMovementService.getStockMovement(params.id)
        render ([stockMovement: stockMovement.toJson()] as JSON)
    }

    def create = { StockMovement stockMovement ->
        stockMovementService.createStockMovement(stockMovement)
	}

    def update = { StockMovement stockMovement ->
        stockMovementService.updateStockMovement(stockMovement)
    }

    def delete = {
        stockMovementService.deleteStockMovement(params.id)
    }

}


class StockMovement {

    String id
    String name
    String description
    Location origin
    Location destination
    Person requestedBy
    Requisition stockList

    Date dateRequested

    List<StockMovementItem> lineItems =
            LazyList.decorate(new ArrayList(), FactoryUtils.instantiateFactory(StockMovementItem.class));


    Requisition requisition
    Order order
    Shipment shipment

    static constraints = {
        id(nullable:true)
        name(nullable:false)
        description(nullable:true)
        origin(nullable:true)
        destination(nullable:false)
        stockList(nullable:true)
        requestedBy(nullable:true)
        dateRequested(nullable:true)
    }


    Map toJson() {
        return [
                id: id,
                name: name,
                description: description,
                origin: origin.id,
                destination: destination.id,
                dateRequested: dateRequested,
                lineItems: lineItems.collect { it.toJson() }
        ]
    }

    static StockMovement createFromShipment(Shipment shipment) {
        StockMovement stockMovement = new StockMovement(
                id: shipment.id,
                name: shipment.name,
                description: shipment.name,
                origin: shipment.origin,
                destination: shipment.destination,
                dateRequested: shipment.dateCreated,
                shipment: shipment
        )

        shipment.shipmentItems.each { shipmentItem ->
            StockMovementItem stockMovementItem = StockMovementItem.createFromShipmentItem(shipmentItem)
            stockMovement.lineItems.add(stockMovementItem)
        }
        return stockMovement
    }
}

class StockMovementItem {

    String id
    String productCode
    BigDecimal quantity

    static constraints = {
        id(nullable:true)
        productCode(nullable:false)
        quantity(nullable:false)
    }

    Map toJson() {
        return [
                id: id,
                productCode: productCode,
                quantity: quantity
        ]
    }

    static StockMovementItem createFromShipmentItem(ShipmentItem shipmentItem) {
        return new StockMovementItem(id: shipmentItem.id, productCode: shipmentItem.product.productCode, quantity: shipmentItem.quantity)
    }

}