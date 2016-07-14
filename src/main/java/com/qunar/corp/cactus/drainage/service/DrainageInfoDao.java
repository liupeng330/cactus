package com.qunar.corp.cactus.drainage.service;

import com.google.common.base.Charsets;
import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;
import com.google.common.hash.Hashing;
import com.qunar.corp.cactus.drainage.bean.DrainageGroup;
import com.qunar.corp.cactus.drainage.bean.DrainageIpAndPort;
import com.qunar.corp.cactus.drainage.bean.DrainageParam;
import com.qunar.corp.cactus.drainage.bean.DrainageServiceInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.*;
import org.springframework.stereotype.Service;

import qunar.tc.dubbocopy.api.model.Group;
import qunar.tc.dubbocopy.api.model.Router;
import qunar.tc.dubbocopy.api.model.Target;

import javax.annotation.Resource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author sen.chai
 * @date 2015-04-22 22:14
 */
@Service
public class DrainageInfoDao {

	@Resource
	private JdbcTemplate jdbcTemplate;

	private static final Logger logger = LoggerFactory.getLogger(DrainageInfoDao.class);
	public static final String LOCK_SERVICE_SQL = "insert ignore into drainage_service_info (drainage_key, service_name, method_name,service_ip, service_port, create_time) values (?, ?, ?, ?, ?, ?)";
	public static final String LOCK_TARGET_SQL = "insert ignore into drainage_target_info (drainage_key, service_name, method_name, group_name, target_list, n, drainage_status ,create_time) values (?, ?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE target_list = ? ,n = ?, drainage_status = ?";
	public static final String UNLOCK_SERVICE_SQL = "delete from drainage_service_info where service_name =  ?";
	public static final String UNLOCK_TARGET_SQL = "delete from drainage_target_info where service_name = ?";
	public static final String QUERY_TARGET_SQL = "select service_name, method_name, group_name, target_list, n, drainage_status from drainage_target_info where create_time >= ? and create_time <= ?";
	public static final String QUERY_COUNT_SQL = "select count(*) from drainage_service_info where service_name=?";

	public static final String QUERY_SERVICE_SQL = "select service_name, method_name, service_ip, service_port from drainage_service_info where service_name =  ?";

	public static final String QUERY_SERVICE_BY_SERVICE_IP_SQL = "select id, drainage_key, service_name, method_name, service_ip, service_port from drainage_service_info where service_ip=? and service_port=?";

	public static final String QUERY_METHOD_SQL = "select method_name from drainage_service_info where service_name = ? limit 0,1";

	public String queryServiceDrainageInfo(String serviceName) {
		return jdbcTemplate.queryForObject(QUERY_METHOD_SQL, new String[]{serviceName}, String.class);
	}
	
	public List<DrainageServiceInfo> queryServiceDrainageInfo(final DrainageIpAndPort ipAndPort) {
		final List<DrainageServiceInfo> result = Lists.newArrayList();
		jdbcTemplate.query(QUERY_SERVICE_BY_SERVICE_IP_SQL, new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement preparedStatement) throws SQLException {
				preparedStatement.setString(1, ipAndPort.getIp());
				preparedStatement.setInt(2, ipAndPort.getPort());
			}
		}, new RowCallbackHandler() {
			@Override
			public void processRow(ResultSet resultSet) throws SQLException {
				DrainageServiceInfo drainageServiceInfo = new DrainageServiceInfo();
				drainageServiceInfo.setServiceIp(resultSet.getString("service_ip"));
				drainageServiceInfo.setServicePort(resultSet.getInt("service_port"));
				drainageServiceInfo.setId(resultSet.getLong("id"));
				drainageServiceInfo.setDrainageKey(resultSet.getString("drainage_key"));
				drainageServiceInfo.setServiceName(resultSet.getString("service_name"));
				drainageServiceInfo.setMethodName(resultSet.getString("method_name"));
				result.add(drainageServiceInfo);
			}
		});
		return result;
	}

	public List<DrainageIpAndPort> queryServiceDrainageInfo(final String serviceName, final String methodName) {
		final List<DrainageIpAndPort> result = Lists.newArrayList();
		jdbcTemplate.query(QUERY_SERVICE_SQL, new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement preparedStatement) throws SQLException {
				preparedStatement.setString(1, serviceName);
			}
		}, new RowCallbackHandler() {
			@Override
			public void processRow(ResultSet resultSet) throws SQLException {
				String serviceIp = resultSet.getString("service_ip");
				int servicePort = resultSet.getInt("service_port");
				result.add(DrainageIpAndPort.of(serviceIp, servicePort));
			}
		});
		return result;
	}

	public void insert(final DrainageParam param) {
		try {
			doInsertService(param, LOCK_SERVICE_SQL, Lists.newArrayList(param.getServiceIpAndPort()));
			// try {
			doInsertTarget(param, LOCK_TARGET_SQL, Lists.newArrayList(param.getTargetGroups()));
			// } catch (DuplicateKeyException e) {
			// logger.info("target已经在引流, target = {}", param.getTargetGroups());
			// }
		} catch (Exception e) {
			logger.error("{} param={}", "insert failed!", param, e);
			throw Throwables.propagate(e);
		}
	}

	private void doInsertService(final DrainageParam param, String sql, final List<DrainageIpAndPort> drainageIpAndPortList) {
		jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
				DrainageIpAndPort ipAndPort = drainageIpAndPortList.get(i);
				final String drainageKey = buildDrainageKey(param, ipAndPort);
				preparedStatement.setString(1, drainageKey);
				preparedStatement.setString(2, param.getServiceName());
				if (param.getMethodName() != null) {
					preparedStatement.setString(3, param.getMethodName());
				} else {
					preparedStatement.setString(3, "");
				}
				preparedStatement.setString(4, ipAndPort.getIp());
				preparedStatement.setInt(5, ipAndPort.getPort());
				preparedStatement.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
			}

			@Override
			public int getBatchSize() {
				return drainageIpAndPortList.size();
			}
		});
	}

	private void doInsertTarget(final DrainageParam param, String sql, final List<DrainageGroup> drainageGroups) {
		jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
				DrainageGroup drainageGroup = drainageGroups.get(i);
				final String drainageKey = buildDrainageKey(param, drainageGroup);
				preparedStatement.setString(1, drainageKey);
				preparedStatement.setString(2, param.getServiceName());
				if (param.getMethodName() != null) {
					preparedStatement.setString(3, param.getMethodName());
				} else {
					preparedStatement.setString(3, "");
				}
				preparedStatement.setString(4, drainageGroup.getGroupName());
				preparedStatement.setString(5, drainageGroup.getTargetList());
				preparedStatement.setInt(6, drainageGroup.getN());
				preparedStatement.setBoolean(7, drainageGroup.isStatus());
				preparedStatement.setTimestamp(8, new Timestamp(System.currentTimeMillis()));
				preparedStatement.setString(9, drainageGroup.getTargetList());
				preparedStatement.setInt(10, drainageGroup.getN());
				preparedStatement.setBoolean(11, drainageGroup.isStatus());
			}

			@Override
			public int getBatchSize() {
				return drainageGroups.size();
			}
		});
	}

	public void delete(final DrainageParam param) {
		try {
			doDelete(param, UNLOCK_SERVICE_SQL);
			doDelete(param, UNLOCK_TARGET_SQL);
		} catch (Exception e) {
			logger.error("{} param={}", "delete failed!", param, e);
			throw Throwables.propagate(e);
		}
	}

	private void doDelete(final DrainageParam param, final String sql) {
		jdbcTemplate.update(sql, new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement preparedStatement) throws SQLException {
				preparedStatement.setString(1, param.getServiceName());
			}
		});
	}

	public int queryCountByServiceName(final String serviceName) {
		return jdbcTemplate.query(QUERY_COUNT_SQL, new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement preparedStatement) throws SQLException {
				preparedStatement.setString(1, serviceName);
			}
		}, new ResultSetExtractor<Integer>() {
			@Override
			public Integer extractData(ResultSet resultSet) throws SQLException, DataAccessException {
				if (resultSet.next()) {
					return resultSet.getInt(1);
				}
				return 0;
			}
		});
	}

	public List<Router> queryAllTargetForDubboCopy() {
		List<DrainageParam> paramList = queryAllTarget();
		List<Router> routers = new ArrayList<Router>();
		for (DrainageParam param : paramList) {
			List<String> methodNames = param.parseMethodNames();
			if (methodNames == null || methodNames.isEmpty()) {
				Router router = new Router();
				router.setServiceName(param.getServiceName());
				router.setMethodName("");
				setGroup(param, router);
				routers.add(router);
			} else {
				for (String methodName : methodNames) {
					Router router = new Router();
					router.setServiceName(param.getServiceName());
					router.setMethodName(methodName);
					setGroup(param, router);
					routers.add(router);
				}
			}
		}
		return routers;
	}

	private void setGroup(DrainageParam param, Router router) {
		Set<Group> groups = Sets.newHashSet(Iterables.transform(param.getTargetGroups(), new Function<DrainageGroup, Group>() {
			public Group apply(DrainageGroup drainageGroup) {
				Group group = new Group(drainageGroup.getGroupName(), drainageGroup.getN());
				group.addAll(Sets.newHashSet(Iterables.transform(drainageGroup.getDrainageIpAndPorts(), new Function<DrainageIpAndPort, Target>() {
					public Target apply(DrainageIpAndPort drainageIpAndPort) {
						return new Target(drainageIpAndPort.getIp(), drainageIpAndPort.getPort());
					}
				})));
				return group;
			}
		}));
		router.setGroups(groups);
	}

	private List<DrainageParam> queryAllTarget() {
		return queryTargetByCreateTimeRange(0, System.currentTimeMillis());
	}

	private List<DrainageParam> queryTargetByCreateTimeRange(final long beginCreateTimeMillis, final long endCreateTimeMillis) {
		final List<DrainageParam> result = Lists.newArrayList();
		final Table<String, String, Set<DrainageGroup>> table = HashBasedTable.create();

		jdbcTemplate.query(QUERY_TARGET_SQL, new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement preparedStatement) throws SQLException {
				preparedStatement.setTimestamp(1, new Timestamp(beginCreateTimeMillis));
				preparedStatement.setTimestamp(2, new Timestamp(endCreateTimeMillis));
			}
		}, new RowCallbackHandler() {
			@Override
			public void processRow(ResultSet resultSet) throws SQLException {
				String serviceName = resultSet.getString("service_name");
				String methodName = resultSet.getString("method_name");
				String groupName = resultSet.getString("group_name");
				String targetList = resultSet.getString("target_list");
				int n = resultSet.getInt("n");
				boolean status = resultSet.getBoolean("drainage_status");
				DrainageGroup drainageGroup = new DrainageGroup(groupName, n, status);
				drainageGroup.setTargetList(targetList);

				Set<DrainageGroup> drainageGroups = table.get(serviceName, methodName);
				if (drainageGroups == null || drainageGroups.isEmpty()) {
					table.put(serviceName, methodName, Sets.newHashSet(drainageGroup));
				} else {
					drainageGroups.add(drainageGroup);
				}
			}
		});

		for (Table.Cell<String, String, Set<DrainageGroup>> cell : table.cellSet()) {
			DrainageParam param = new DrainageParam();
			param.setServiceName(cell.getRowKey());
			param.setMethodName(cell.getColumnKey());
			param.setTargetGroups(cell.getValue());
			result.add(param);
		}

		return result;
	}

	private String buildDrainageKey(final DrainageParam param, DrainageIpAndPort ipAndPort) {
		return getDrainageKeyPrefix(param.getServiceName(), param.getMethodName()) + ipAndPort.getIp() + ":" + ipAndPort.getPort();
	}

	private String buildDrainageKey(final DrainageParam param, DrainageGroup drainageGroup) {
		return getDrainageKeyPrefix(param.getServiceName(), param.getMethodName()) + drainageGroup.getGroupName();
	}

	private String getDrainageKeyPrefix(String serviceName, String methodName) {
		if (Strings.isNullOrEmpty(methodName)) {
			return Hashing.md5().hashString(serviceName, Charsets.UTF_8).toString();
		} else {
			return Hashing.md5().hashString(serviceName + ":" + methodName, Charsets.UTF_8).toString();
		}
	}

}
