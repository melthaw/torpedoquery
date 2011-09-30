package com.netappsid.jpaquery;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javassist.util.proxy.ProxyFactory;

import com.netappsid.jpaquery.internal.ArrayCallHandler;
import com.netappsid.jpaquery.internal.ArrayCallHandler.ValueHandler;
import com.netappsid.jpaquery.internal.AscFunctionHandler;
import com.netappsid.jpaquery.internal.AvgFunctionHandler;
import com.netappsid.jpaquery.internal.CoalesceFunction;
import com.netappsid.jpaquery.internal.ComparableConstantFunctionHandler;
import com.netappsid.jpaquery.internal.ConstantFunctionHandler;
import com.netappsid.jpaquery.internal.CountFunctionHandler;
import com.netappsid.jpaquery.internal.DescFunctionHandler;
import com.netappsid.jpaquery.internal.DistinctFunctionHandler;
import com.netappsid.jpaquery.internal.DoNothingQueryConfigurator;
import com.netappsid.jpaquery.internal.FJPAMethodHandler;
import com.netappsid.jpaquery.internal.GroupBy;
import com.netappsid.jpaquery.internal.GroupingConditionHandler;
import com.netappsid.jpaquery.internal.InnerJoinHandler;
import com.netappsid.jpaquery.internal.LeftJoinHandler;
import com.netappsid.jpaquery.internal.MaxFunctionHandler;
import com.netappsid.jpaquery.internal.MinFunctionHandler;
import com.netappsid.jpaquery.internal.MultiClassLoaderProvider;
import com.netappsid.jpaquery.internal.Proxy;
import com.netappsid.jpaquery.internal.ProxyFactoryFactory;
import com.netappsid.jpaquery.internal.QueryBuilder;
import com.netappsid.jpaquery.internal.RightJoinHandler;
import com.netappsid.jpaquery.internal.Selector;
import com.netappsid.jpaquery.internal.SumFunctionHandler;
import com.netappsid.jpaquery.internal.WhereClauseHandler;
import com.netappsid.jpaquery.internal.WhereQueryConfigurator;

public class FJPAQuery {
	private static ThreadLocal<Proxy> query = new ThreadLocal<Proxy>();
	private static final ProxyFactoryFactory proxyFactoryFactory = new ProxyFactoryFactory(new MultiClassLoaderProvider());

	public static <T> T from(Class<T> toQuery) {
		try {
			final ProxyFactory proxyFactory = proxyFactoryFactory.getProxyFactory();

			proxyFactory.setSuperclass(toQuery);
			proxyFactory.setInterfaces(new Class[] { Proxy.class });

			QueryBuilder queryBuilder = new QueryBuilder(toQuery);
			FJPAMethodHandler fjpaMethodHandler = new FJPAMethodHandler(queryBuilder, proxyFactoryFactory);
			final T proxy = (T) proxyFactory.create(null, null, fjpaMethodHandler);

			fjpaMethodHandler.addQueryBuilder(proxy, queryBuilder);

			setQuery((Proxy) proxy);
			return proxy;

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static <T, E extends T> E extend(T toExtend, Class<E> extendClass) {
		try {
			final ProxyFactory proxyFactory = proxyFactoryFactory.getProxyFactory();

			proxyFactory.setSuperclass(extendClass);
			proxyFactory.setInterfaces(new Class[] { Proxy.class });

			FJPAMethodHandler fjpaMethodHandler = FJPAQuery.getFJPAMethodHandler();
			final E proxy = (E) proxyFactory.create(null, null, fjpaMethodHandler);

			QueryBuilder queryBuilder = fjpaMethodHandler.getQueryBuilder(toExtend);
			fjpaMethodHandler.addQueryBuilder(proxy, queryBuilder);

			return proxy;

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static <T> Query<T> select(Function<T> value) {
		return (Query<T>) FJPAQuery.select(new Object[] { value });
	}

	public static <T> Query<T> select(T value) {
		return (Query<T>) FJPAQuery.select(new Object[] { value });
	}

	public static <T> Query<T[]> select(Function<T>... values) {
		return select((T[]) values);
	}

	public static <T> Query<T[]> select(T... values) {
		FJPAMethodHandler fjpaMethodHandler = getFJPAMethodHandler();
		final QueryBuilder root = fjpaMethodHandler.getRoot();
		fjpaMethodHandler.handle(new ArrayCallHandler(new ValueHandler() {

			@Override
			public void handle(Proxy query, QueryBuilder queryBuilder, Selector selector) {
				root.addSelector(selector);
			}
		}, values));

		return root;

	}

	public static <T> T innerJoin(T toJoin) {
		return getFJPAMethodHandler().handle(new InnerJoinHandler<T>(getFJPAMethodHandler(), proxyFactoryFactory));
	}

	public static <T, E extends T> E innerJoin(T toJoin, Class<E> realType) {
		return getFJPAMethodHandler().handle(new InnerJoinHandler<E>(getFJPAMethodHandler(), proxyFactoryFactory, realType));
	}

	public static <T> T innerJoin(Collection<T> toJoin) {
		return getFJPAMethodHandler().handle(new InnerJoinHandler<T>(getFJPAMethodHandler(), proxyFactoryFactory));
	}

	public static <T, E extends T> E innerJoin(Collection<T> toJoin, Class<E> realType) {
		return getFJPAMethodHandler().handle(new InnerJoinHandler<E>(getFJPAMethodHandler(), proxyFactoryFactory, realType));
	}

	public static <T> T innerJoin(Map<?, T> toJoin) {
		return getFJPAMethodHandler().handle(new InnerJoinHandler<T>(getFJPAMethodHandler(), proxyFactoryFactory));
	}

	public static <T, E extends T> E innerJoin(Map<?, T> toJoin, Class<E> realType) {
		return getFJPAMethodHandler().handle(new InnerJoinHandler<E>(getFJPAMethodHandler(), proxyFactoryFactory, realType));
	}

	public static <T> T leftJoin(T toJoin) {
		return getFJPAMethodHandler().handle(new LeftJoinHandler<T>(getFJPAMethodHandler(), proxyFactoryFactory));
	}

	public static <T, E extends T> E leftJoin(T toJoin, Class<E> realType) {
		return getFJPAMethodHandler().handle(new LeftJoinHandler<E>(getFJPAMethodHandler(), proxyFactoryFactory, realType));
	}

	public static <T> T leftJoin(Collection<T> toJoin) {
		return getFJPAMethodHandler().handle(new LeftJoinHandler<T>(getFJPAMethodHandler(), proxyFactoryFactory));
	}

	public static <T, E extends T> E leftJoin(Collection<T> toJoin, Class<E> realType) {
		return getFJPAMethodHandler().handle(new LeftJoinHandler<E>(getFJPAMethodHandler(), proxyFactoryFactory, realType));
	}

	public static <T> T leftJoin(Map<?, T> toJoin) {
		return getFJPAMethodHandler().handle(new LeftJoinHandler<T>(getFJPAMethodHandler(), proxyFactoryFactory));
	}

	public static <T, E extends T> E leftJoin(Map<?, T> toJoin, Class<E> realType) {
		return getFJPAMethodHandler().handle(new LeftJoinHandler<E>(getFJPAMethodHandler(), proxyFactoryFactory, realType));
	}

	public static <T> T rightJoin(T toJoin) {
		return getFJPAMethodHandler().handle(new RightJoinHandler<T>(getFJPAMethodHandler(), proxyFactoryFactory));
	}

	public static <T, E extends T> E rightJoin(T toJoin, Class<E> realType) {
		return getFJPAMethodHandler().handle(new RightJoinHandler<E>(getFJPAMethodHandler(), proxyFactoryFactory, realType));
	}

	public static <T> T rightJoin(Collection<T> toJoin) {
		return getFJPAMethodHandler().handle(new RightJoinHandler<T>(getFJPAMethodHandler(), proxyFactoryFactory));
	}

	public static <T, E extends T> E rightJoin(Collection<T> toJoin, Class<E> realType) {
		return getFJPAMethodHandler().handle(new RightJoinHandler<E>(getFJPAMethodHandler(), proxyFactoryFactory, realType));
	}

	public static <T> T rightJoin(Map<?, T> toJoin) {
		return getFJPAMethodHandler().handle(new RightJoinHandler<T>(getFJPAMethodHandler(), proxyFactoryFactory));
	}

	public static <T, E extends T> E rightJoin(Map<?, T> toJoin, Class<E> realType) {
		return getFJPAMethodHandler().handle(new RightJoinHandler<E>(getFJPAMethodHandler(), proxyFactoryFactory, realType));
	}

	public static <T> OnGoingLogicalCondition where(OnGoingLogicalCondition condition) {
		return getFJPAMethodHandler().handle(new GroupingConditionHandler<T>(new WhereQueryConfigurator<T>(), condition));
	}

	public static <T> ValueOnGoingCondition<T> where(T object) {
		return getFJPAMethodHandler().handle(new WhereClauseHandler<T, ValueOnGoingCondition<T>>());
	}

	public static <V, T extends Comparable<V>> OnGoingComparableCondition<V> where(T object) {
		return getFJPAMethodHandler().handle(new WhereClauseHandler<V, OnGoingComparableCondition<V>>());
	}

	public static OnGoingStringCondition<String> where(String object) {
		return getFJPAMethodHandler().handle(new WhereClauseHandler<String, OnGoingStringCondition<String>>());
	}

	public static <T> OnGoingCollectionCondition<T> where(Collection<T> object) {
		return getFJPAMethodHandler().handle(new WhereClauseHandler<T, OnGoingCollectionCondition<T>>(new WhereQueryConfigurator<T>()));
	}

	public static <T> ValueOnGoingCondition<T> with(T object) {
		return getFJPAMethodHandler().handle(new WhereClauseHandler<T, ValueOnGoingCondition<T>>(new WithQueryConfigurator<T>()));
	}

	public static <V, T extends Comparable<V>> OnGoingComparableCondition<V> with(T object) {
		return getFJPAMethodHandler().handle(new WhereClauseHandler<V, OnGoingComparableCondition<V>>(new WithQueryConfigurator<V>()));
	}

	public static OnGoingStringCondition<String> with(String object) {
		return getFJPAMethodHandler().handle(new WhereClauseHandler<String, OnGoingStringCondition<String>>(new WithQueryConfigurator<String>()));
	}

	public static <T> OnGoingCollectionCondition<T> with(Collection<T> object) {
		return getFJPAMethodHandler().handle(new WhereClauseHandler<T, OnGoingCollectionCondition<T>>(new WithQueryConfigurator<T>()));
	}

	public static <T> OnGoingLogicalCondition with(OnGoingLogicalCondition condition) {
		return getFJPAMethodHandler().handle(new GroupingConditionHandler<T>(new WithQueryConfigurator<T>(), condition));
	}

	public static <T> ValueOnGoingCondition<T> condition(T object) {
		return getFJPAMethodHandler().handle(new WhereClauseHandler<T, ValueOnGoingCondition<T>>(new DoNothingQueryConfigurator<T>()));
	}

	public static <V, T extends Comparable<V>> OnGoingComparableCondition<V> condition(T object) {
		return getFJPAMethodHandler().handle(new WhereClauseHandler<V, OnGoingComparableCondition<V>>(new DoNothingQueryConfigurator<V>()));
	}

	public static OnGoingStringCondition<String> condition(String object) {
		return getFJPAMethodHandler().handle(new WhereClauseHandler<String, OnGoingStringCondition<String>>(new DoNothingQueryConfigurator<String>()));
	}

	public static <T> OnGoingCollectionCondition<T> condition(Collection<T> object) {
		return getFJPAMethodHandler().handle(new WhereClauseHandler<T, OnGoingCollectionCondition<T>>(new DoNothingQueryConfigurator<T>()));
	}

	public static <T> OnGoingLogicalCondition condition(OnGoingLogicalCondition condition) {
		return getFJPAMethodHandler().handle(new GroupingConditionHandler<T>(new DoNothingQueryConfigurator<T>(), condition));
	}

	public static OnGoingGroupByCondition groupBy(Object... values) {

		FJPAMethodHandler fjpaMethodHandler = getFJPAMethodHandler();
		final QueryBuilder root = fjpaMethodHandler.getRoot();
		final GroupBy groupBy = new GroupBy();

		fjpaMethodHandler.handle(new ArrayCallHandler(new ValueHandler() {
			@Override
			public void handle(Proxy proxy, QueryBuilder queryBuilder, Selector selector) {
				groupBy.addGroup(selector);
			}
		}, values));

		root.setGroupBy(groupBy);
		return groupBy;
	}

	// JPA Functions
	public static Function<Long> count(Object object) {
		if (object instanceof Proxy) {
			setQuery((Proxy) object);
		}
		return getFJPAMethodHandler().handle(new CountFunctionHandler(object));
	}

	public static <V, T extends Comparable<V>> ComparableFunction<V> sum(T number) {
		return getFJPAMethodHandler().handle(new SumFunctionHandler<V>());
	}

	public static <V, T extends Comparable<V>> ComparableFunction<V> min(T number) {
		return getFJPAMethodHandler().handle(new MinFunctionHandler<V>());
	}

	public static <V, T extends Comparable<V>> ComparableFunction<V> max(T number) {
		return getFJPAMethodHandler().handle(new MaxFunctionHandler<V>());
	}

	public static <V, T extends Comparable<V>> ComparableFunction<V> avg(T number) {
		return getFJPAMethodHandler().handle(new AvgFunctionHandler<V>());
	}

	public static <T, E extends Function<T>> E coalesce(E... values) {
		CoalesceFunction<E> coalesceFunction = getCoalesceFunction(values);
		return (E) coalesceFunction;
	}

	public static <T> Function<T> coalesce(T... values) {
		final CoalesceFunction<T> coalesceFunction = getCoalesceFunction(values);
		return coalesceFunction;
	}

	private static <T> CoalesceFunction<T> getCoalesceFunction(T... values) {
		final CoalesceFunction coalesceFunction = new CoalesceFunction();
		getFJPAMethodHandler().handle(new ArrayCallHandler(new ValueHandler() {
			@Override
			public void handle(Proxy proxy, QueryBuilder queryBuilder, Selector selector) {
				coalesceFunction.setQuery(proxy);
				coalesceFunction.addSelector(selector);
			}
		}, values));
		return coalesceFunction;
	}

	public static <T> Function<T> distinct(T object) {
		if (object instanceof Proxy) {
			setQuery((Proxy) object);
		}
		return getFJPAMethodHandler().handle(new DistinctFunctionHandler<T>(object));
	}

	public static <T> Function<T> constant(T constant) {
		return getFJPAMethodHandler().handle(new ConstantFunctionHandler<T>(constant));
	}

	public static <V, T extends Comparable<V>> ComparableFunction<T> constant(T constant) {
		return getFJPAMethodHandler().handle(new ComparableConstantFunctionHandler<T>(constant));
	}

	public static void orderBy(Object... values) {
		getFJPAMethodHandler().handle(new ArrayCallHandler(new ValueHandler() {
			@Override
			public void handle(Proxy proxy, QueryBuilder queryBuilder, Selector selector) {
				queryBuilder.addOrder(selector);
			}
		}, values));

	}

	// orderBy function

	public static Function asc(Object object) {
		return getFJPAMethodHandler().handle(new AscFunctionHandler());
	}

	public static Function desc(Object object) {
		return getFJPAMethodHandler().handle(new DescFunctionHandler());
	}

	public static String query(Object proxy) {
		if (proxy instanceof Proxy) {
			Proxy from = (Proxy) proxy;
			return from.getFJPAMethodHandler().<QueryBuilder> getRoot().getQuery(new AtomicInteger());
		}
		return null;
	}

	public static Map<String, Object> params(Object proxy) {
		if (proxy instanceof Proxy) {
			Proxy from = (Proxy) proxy;
			return from.getFJPAMethodHandler().<QueryBuilder> getRoot().getParameters();
		}
		return null;
	}

	public static void setQuery(Proxy query) {
		FJPAQuery.query.set(query);
	}

	// TODO devrait se retrouver dans l'api interne

	public static FJPAMethodHandler getFJPAMethodHandler() {
		Proxy internalQuery = query.get();
		return internalQuery.getFJPAMethodHandler();
	}

}