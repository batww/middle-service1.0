package com.lexor.service.item;

import com.intuit.ipp.data.Item;
import com.intuit.ipp.exception.FMSException;


import java.io.IOException;

/**
 * @author auphan
 */

public interface IItemQBOService {
    Item createItem(Object product, int i) throws FMSException, IOException;
    Item getItemById(String id);

}
