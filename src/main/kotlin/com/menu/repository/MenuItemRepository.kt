package com.menu.repository

import com.menu.model.MenuItem
import org.springframework.data.mongodb.repository.MongoRepository

interface MenuItemRepository : MongoRepository<MenuItem, String>
