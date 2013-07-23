TorpedoQuery
============
[![Build Status](https://secure.travis-ci.org/xjodoin/torpedoquery.png?branch=master)](http://travis-ci.org/xjodoin/torpedoquery)

Torpedo Query goal is to simplify how you create and maintain your HQL query.
 
#### Maven ####
```xml
<dependency>
		<groupId>org.torpedoquery</groupId>
		<artifactId>org.torpedoquery</artifactId>
		<version>1.5.1</version>
</dependency>
```
 
  
#### Quick start ####

  	First add this import static org.torpedoquery.jpa.Torpedo.*;
  
  	1. Create simple select
  		
  		Entity entity = from(Entity.class);
 		org.torpedoquery.jpa.Query<Entity> select = select(entity);
 		
 	2. Create scalar queries
 
 		Entity entity = from(Entity.class);
 		org.torpedoquery.jpa.Query<String> select = select(entity.getCode());	
 
   	3. How to execute your query
   
   		Entity entity = from(Entity.class);
 		org.torpedoquery.jpa.Query<Entity> select = select(entity);
 		List<Entity> entityList = select.list(entityManager);
 
 	4. Create simple condition
 
 		Entity entity = from(Entity.class);
 		where(entity.getCode()).eq("mycode");
 		org.torpedoquery.jpa.Query<Entity> select = select(entity);
 
 	5. Create join on your entities
 
 		Entity entity = from(Entity.class);
 		SubEntity subEntity = innerJoin(entity.getSubEntities());
 		org.torpedoquery.jpa.Query<String[]> select = select(entity.getCode(), subEntity.getName());
 
   	6. Group your conditions
   
   		Entity from = from(Entity.class);
 		OnGoingLogicalCondition condition = condition(from.getCode()).eq("test").or(from.getCode()).eq("test2");
 		where(from.getName()).eq("test").and(condition);
 		Query<Entity> select = select(from);



[![githalytics.com alpha](https://cruel-carlota.pagodabox.com/2d5ab2cb91c7289d22767c22616eb063 "githalytics.com")](http://githalytics.com/xjodoin/torpedoquery)
