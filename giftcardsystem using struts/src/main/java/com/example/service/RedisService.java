package com.example.service;

import java.util.List;
import java.util.Set;
import java.sql.Time;
import java.util.ArrayList;

import com.example.model.GiftCard;
import com.example.model.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisService {
	
	
	private static final int EXPIRY_TIME = 1800;
//	private static final String REDIS_PASSWORD = "admin";
	// 30 minutes
	private static final ObjectMapper objectMapper = new ObjectMapper();

	private static final JedisPool jedisPool;

	static {
		objectMapper.registerModule(new JavaTimeModule());

		JedisPoolConfig poolConfig = new JedisPoolConfig();
		poolConfig.setMaxTotal(10); // default 8
		poolConfig.setMaxIdle(5);
		poolConfig.setMinIdle(2);
		poolConfig.setBlockWhenExhausted(true); // true wait for the connection
		poolConfig.setMaxWaitMillis(5000); // Wait up to 5 seconds for a connection, -1 means infinite wait
		// JedisExhaustedPoolException

		jedisPool = new JedisPool(poolConfig, "localhost", 6379);
	}

	public static void closeJedisPool() {
		if (jedisPool != null) {
			jedisPool.close();
			System.out.println("JedisPool closed successfully.");
		} else {
			System.out.println("JedisPool is already closed or not initialized.");
		}
	}
	
	public static Jedis getConnection() {
		return jedisPool.getResource();
	}

	public static void saveGiftCardToCache(long userId, List<GiftCard> giftCards) {
		try (Jedis jedis = jedisPool.getResource()) {

			for (GiftCard card : giftCards) {
				String cardJson = objectMapper.writeValueAsString(card);
				jedis.hset("giftCardList:" + userId, card.getGiftCardId() + "", cardJson);

				String statusKey = "giftCardListWithStatus" + card.getStatus() + ":" + userId;
				jedis.sadd(statusKey, card.getGiftCardId() + "");
				jedis.expire("giftCardList:" + userId, EXPIRY_TIME);
				jedis.expire(statusKey, EXPIRY_TIME);
			}
		} catch (JsonProcessingException e) {
			System.out.println("Error saving data to Redis: " + e.getMessage());
		}
	}

	// hset giftcardList:3 {{1001, {id, amount,pin}},{1002, {id, amount,pin}}}
	// set giftcardListstatus1:3 {1001, 1002,1003}
	// set giftcardListstatus0:3 {1005, 1006,1007}

	public static List<GiftCard> getGiftCardsByStatus(long userId, Integer status) {
		List<GiftCard> giftCards = new ArrayList<>();

		try (Jedis jedis = jedisPool.getResource()) {
//			jedis.auth(REDIS_PASSWORD);
			if (status != null) {
				String statusKey = "giftCardListWithStatus" + status + ":" + userId;
				Set<String> cardIds = jedis.smembers(statusKey);

				System.out.println("cardId set: " + cardIds);
				if (cardIds == null) {
					return null;
				}
				if (cardIds.isEmpty()) {
					return null;
				}
				for (String cardId : cardIds) {
					String cardJson = jedis.hget("giftCardList:" + userId, cardId);

					if (cardJson != null) {
						GiftCard card = objectMapper.readValue(cardJson, GiftCard.class);
						giftCards.add(card);
					} else {
						return null;
					}

				}

			} else {
				String statusKey = "giftCardList:" + userId;
				Set<String> cardIds = jedis.hkeys(statusKey);

				if (cardIds.isEmpty())
					return null;
				for (String cardId : cardIds) {
					String cardJson = jedis.hget(statusKey, cardId);
					if (cardJson != null) {
						GiftCard card = objectMapper.readValue(cardJson, GiftCard.class);
						giftCards.add(card);
					}
				}

			}
		} catch (Exception e) {
			System.out.println("Error retrieving gift cards from Redis: " + e.getMessage());
		}

		return giftCards;
	}

	public static void removeFromCache(String key) {
		try (Jedis jedis = jedisPool.getResource()) {
//			jedis.auth(REDIS_PASSWORD);
			if (jedis.exists(key)) {
				jedis.del(key);
				System.out.println("Key deleted from Redis: " + key);
			} else {
				System.out.println("Key not found in Redis: " + key);
			}
		} catch (Exception e) {
			System.out.println("Redis deletion error: " + e.getMessage());
		}
	}

	public static void saveUserListToCache(List<User> userList) {
		try (Jedis jedis = jedisPool.getResource()) {

			for (User user : userList) {
				String userString = objectMapper.writeValueAsString(user);
				jedis.hset("userList", user.getUserId() + "", userString);

				jedis.expire("userList", EXPIRY_TIME);
			}

			System.out.println("userList is stroed in the cache");
		} catch (JsonProcessingException e) {
			System.out.println("Error saving data to Redis: " + e.getMessage());
		}
	}

	public static void updatedCard(GiftCard updatedCard, long userId, String update, Integer previousStatus) {

		try (Jedis jedis = jedisPool.getResource()) {

			String cardJson = objectMapper.writeValueAsString(updatedCard);

			if (jedis.exists("giftCardList:" + userId)) {

				jedis.hset("giftCardList:" + userId, String.valueOf(updatedCard.getGiftCardId()), cardJson);
				if (update.equals("blocked")) {
					jedis.srem("giftCardListWithStatus" + previousStatus + ":" + userId,
							String.valueOf(updatedCard.getGiftCardId()));
					jedis.sadd("giftCardListWithStatus" + updatedCard.getStatus() + ":" + userId,
							String.valueOf(updatedCard.getGiftCardId()), cardJson);
				}

			}

		} catch (JsonProcessingException e) {
			System.out.println("Error saving data to Redis: " + e.getMessage());
		}

	}

	public static List<User> getUserListFromCache() {
		List<User> userList = new ArrayList<>();

		try (Jedis jedis = jedisPool.getResource()) {

			Set<String> users = jedis.hkeys("userList");
			System.out.println("userHash: " + users);

			if (users == null || users.isEmpty()) {
				return userList;
			}

			for (String user : users) {
				String c = jedis.hget("userList", user);

				System.out.println("c: " + c);
				User userObj = objectMapper.readValue(c, User.class);
				userList.add(userObj);

			}
		} catch (Exception e) {
			System.out.println("Error retrieving users from Redis: " + e.getMessage());
		}

		System.out.println("userList: " + userList);
		return userList;
	}

	public static void addUserToList(User user) {
		try (Jedis jedis = jedisPool.getResource()) {
//			jedis.auth(REDIS_PASSWORD);
			System.out.println("User list updated...");

			String userString = objectMapper.writeValueAsString(user);
			jedis.hset("userList", user.getUserId() + "", userString);
		} catch (JsonProcessingException e) {
			System.out.println("Error saving data to Redis: " + e.getMessage());
		}
	}

	public static void removeUserList() { // remove the userList form the redis
		try (Jedis jedis = jedisPool.getResource()) {
//			jedis.auth(REDIS_PASSWORD);
			System.out.println("userlist deleted...");
			if (jedis.exists("userList")) {// hset userList = {userId,userJsonString}, {userId,userJsonString}
				jedis.del("userList");
			}

		} catch (Exception e) {
			System.out.println("Error saving data to Redis: " + e.getMessage());
		}

	}

	public static void removeUserData(long userId) { // remove the specific user card form the redis
		try (Jedis jedis = jedisPool.getResource()) {
//			jedis.auth(REDIS_PASSWORD);
			String userGiftCardKey = "giftCardList:" + userId;
			if (jedis.exists(userGiftCardKey)) {
				jedis.del(userGiftCardKey);
			}

			String statusKey = "giftCardListWithStatus*:" + userId;
			if (jedis.exists(statusKey)) {
				jedis.del(statusKey);
			}

			System.out.println("User data removed successfully for userId: " + userId);
		} catch (Exception e) {
			System.out.println("Redis deletion error: " + e.getMessage());
		}
	}

	public static int temporarilyBlockedCards(long cardNumber) {
		try (Jedis jedis = jedisPool.getResource()) {
//			jedis.auth(REDIS_PASSWORD);
			int count = 0;
			String cardKey = "tempblock:" + cardNumber;

			if (jedis.exists(cardKey)) {
				String value = jedis.get(cardKey);

				if (value != null) {
					count = Integer.parseInt(value);
				}

				jedis.incr(cardKey);
				count += 1;
				System.out.println(cardNumber + " invalid pin count: " + count);

				long ttl = jedis.ttl(cardKey);
				System.out.println("TTL of " + cardKey + ": " + ttl + " seconds");

//				jedis.setex(cardKey, (int) ttl, String.valueOf(count)); 

				if (count >= 3) {
					return count;
				}
			} else {
				jedis.setex(cardKey, 60, "1");
				System.out.println("Card " + cardNumber + " is stored in Redis for the first time.");
				count = 1;
			}

			return count;
		}
	}
}
/*
 * 
 * jedis.exists("name"); jedis.del("key"); jedis.setex(key, timeinsecons, value)
 * 
 * JedisPoolConfig poolConfig = new JedisPoolConfig();
 * poolConfig.setMaxTotal(50); // Max 50 connections allowed in the pool
 * JedisPool jedisPool = new JedisPool(poolConfig, "localhost");
 * poolConfig.setMaxWaitMillis(5000); // Wait up to 5 seconds for a connection
 *
 *
 *
 * @JsonIgnoreProperties(ignoreUnknown = true) or ObjectMapper mapper = new
 * ObjectMapper();
 * mapper.configure(DeserilizatioFeature.FAILS_ON_UNKNOWN_PROPERTIES, false);
 *
 *
 *
 * JedisPoolConfig poolConfig = new JedisPoolConfig();
 * poolConfig.setMaxTotal(50); // Set max connections in the pool
 * poolConfig.setMaxIdle(10); // Set max idle connections
 * poolConfig.setMinIdle(2); // Set min idle connections
 * poolConfig.setBlockWhenExhausted(true); // Block if pool is exhausted
 * poolConfig.setMaxWaitMillis(5000); // Wait max 5 sec before throwing
 * exception
 */

//public static <T> T getFromCache(String key, TypeReference<T> type) {
////Jedis obj = new Jedis("localhost");
//try (Jedis jedis = jedisPool.getResource()) {
//String cachedData = jedis.get(key);
//if (cachedData != null) {
//	System.out.println("Data fetched from Redis cache.");
//	jedis.expire(key, EXPIRY_TIME);
//
//	List list = objectMapper.readValue(cachedData, List.class);
//	System.out.println("list type " + list.getClass().getName() + " list" + list);
//
//	return objectMapper.readValue(cachedData, type);
//}
//} catch (Exception e) {
//System.out.println("Redis error: " + e.getMessage());
//}
//return null;
//}
//
//public static <T> void saveToCache(String key, T data) {
//try (Jedis jedis = jedisPool.getResource()) {
//String jsonData = objectMapper.writeValueAsString(data);
//jedis.setex(key, EXPIRY_TIME, jsonData);
//} catch (JsonProcessingException e) {
//System.out.println("Error saving data to Redis: " + e.getMessage());
//}
//}
