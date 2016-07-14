package com.qunar.corp.cactus.service.governance;

import com.alibaba.dubbo.common.URL;
import com.google.common.base.*;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.eventbus.EventBus;
import com.qunar.corp.cactus.bean.*;
import com.qunar.corp.cactus.checker.CommonChecker;
import com.qunar.corp.cactus.dao.GovernanceDataDao;
import com.qunar.corp.cactus.event.EventType;
import com.qunar.corp.cactus.event.UrlChangeEvent;
import com.qunar.corp.cactus.exception.EmptyParamException;
import com.qunar.corp.cactus.exception.EnableToDeleteException;
import com.qunar.corp.cactus.service.AbstractService;
import com.qunar.corp.cactus.service.providerlevel.ProviderService;
import com.qunar.corp.cactus.support.protocol.GovernanceFactory;
import com.qunar.corp.cactus.support.UserContainer;
import com.qunar.corp.cactus.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.base.Strings.nullToEmpty;
import static com.qunar.corp.cactus.util.ConstantHelper.*;

/**
 * Created by IntelliJ IDEA.
 * User: liuzz
 * Date: 13-12-23
 * Time: 下午12:08
 */
public abstract class AbstractGovernanceService extends AbstractService implements IGovernanceService {

    private static final Logger logger = LoggerFactory.getLogger(AbstractGovernanceService.class);

    @Resource
    protected CommonChecker commonChecker;

    @Resource
    protected GovernanceDataDao dao;

    @Resource
    protected ProviderService providerService;

    @Resource
    protected IQueryGovernanceService queryService;

    protected GovernanceFactory governanceFactory;

    protected AbstractGovernanceService(GovernanceFactory governanceFactory) {
        this.governanceFactory = governanceFactory;
    }

    @Override
    @Transactional
    public final List<Long> addDatas(List<ServiceSign> signs, Map<String, String> params) {
        logger.info("add config or router: sign is {}, params is {}", signs, params);
        List<Long> ids = Lists.newArrayList();
        if (signs == null || signs.isEmpty()) {
            logger.info("no service sign is picked up");
            return ids;
        }

        ServiceSign aSign = signs.get(0);
        commonChecker.checkValidState(aSign);

        Map<String, String> realParams = preprocess(signs, params);
        checkAddDatas(signs, params);
        if (isTrivialParams(realParams)) {
            logger.info("no param need to be deal");
            return ids;
        }

        List<GovernanceData> datas = FluentIterable.from(signs).transform(toGovernanceData(realParams)).toList();
        ids = dao.batchInsert(datas);
        return ids;
    }

    protected void checkAddDatas(List<ServiceSign> signs, Map<String, String> params) {

    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public final boolean changeDatas(GovernanceData data, Map<String, String> params) {
        logger.info("change config or router data: {}{}for params: {}", data, NEW_LINE, params);
        checkDisableStatus(data);

        ServiceSign sign = data.getServiceSign();
        params.put(NAME, data.getUrl().getParameter(NAME));
        Map<String, String> realParams = preprocess(Arrays.asList(sign), params);
        checkNotEmptyParam(realParams);

        GovernanceData newData = toGovernanceData(realParams).apply(sign).setId(data.getId());
        if (Objects.equal(newData.getUrlStr(), data.getUrlStr())) {
            return false;
        }

        dao.update(newData);
        return true;
    }

    private void checkDisableStatus(GovernanceData data) {
        if (data.getStatus() != Status.DISABLE) {
            logger.error("data is not disabled ({})", data);
            throw new IllegalArgumentException("data is not disabled (" + data + ")");
        }
    }

    private void checkNotEmptyParam(Map<String, String> realParams) {
        if (isTrivialParams(realParams)) {
            logger.info("no param need to be deal");
            throw new EmptyParamException("no params");
        }
    }

    private GovernanceData makeLogData(GovernanceData data, GovernanceData newData) {
        return data.copy().setUrl(data.getUrl().addParameter(CACTUS_LOG_NEW_DATA, URL.encode(newData.getUrlStr())));
    }

    protected Map<String, String> removeEmptyParams(Map<String, String> params) {
        Map<String, String> realParams = Maps.newHashMap();
        if (params == null || params.isEmpty()) {
            return realParams;
        }
        for (Map.Entry<String, String> entry : params.entrySet()) {
            String key = nullToEmpty(entry.getKey()).trim();
            String value = COMMA_JOINER.join(COMMA_SPLITTER.split(nullToEmpty(entry.getValue())));
            if (!isNullOrEmpty(key) && !isNullOrEmpty(value)) {
                realParams.put(key, value);
            }
        }
        return realParams;
    }

    protected static final Splitter COMMA_SPLITTER = Splitter.on(',').trimResults().omitEmptyStrings();

    protected static final Joiner COMMA_JOINER = Joiner.on(',');

    protected abstract Map<String, String> preprocess(List<ServiceSign> signs, Map<String, String> params);

    protected Function<ServiceSign, GovernanceData> toGovernanceData(final Map<String, String> params) {
        return new Function<ServiceSign, GovernanceData>() {
            @Override
            public GovernanceData apply(ServiceSign sign) {
                return governanceFactory.create(sign, params);
            }
        };
    }

    @Override
    @Transactional
    public final void enable(GovernanceData data) {
        logger.info("enable data: {}", data);
        commonChecker.checkValidState(data.getServiceSign());
        checkEnable(data);
        dealUserDataStatusChange(data, Status.ENABLE);
    }

    protected void checkEnable(GovernanceData data) {

    }

    protected void writeDatasChange(Result result) {
        Pair<List<GovernanceData>, List<GovernanceData>> insertDatasAndUpdateDatas =
                getInsertDatasAndUpdateDatas(result);
        dao.batchInsert(dealLastOperator(insertDatasAndUpdateDatas.left));
        dao.batchUpdate(dealLastOperator(insertDatasAndUpdateDatas.right));
    }

    protected List<GovernanceData> dealLastOperator(List<GovernanceData> datasToDBUpdate) {
        List<GovernanceData> resultDatas = Lists.newArrayListWithCapacity(datasToDBUpdate.size());
        for (GovernanceData data : datasToDBUpdate) {
            resultDatas.add(dealLastOperator(data));
        }
        return resultDatas;
    }

    protected GovernanceData dealLastOperator(GovernanceData data) {
        if (Objects.equal(data.getLastOperator(), UserContainer.getUserName())) {
            return data;
        } else {
            return data.copy().setLastOperator(UserContainer.getUserName());
        }
    }

    protected Pair<List<GovernanceData>, List<GovernanceData>> getInsertDatasAndUpdateDatas(Result result) {
        List<GovernanceData> insertDatas = Lists.newArrayList();
        List<GovernanceData> updateDatas = Lists.newArrayList();
        Map<EventType, List<GovernanceData>> datasChange = result.getDatasChange();
        for (EventType type : datasChange.keySet()) {
            List<GovernanceData> datas = datasChange.get(type);
            if (datas != null) {
                if (type == EventType.NEW) {
                    insertDatas.addAll(getResultDatas(type, datas));
                } else {
                    updateDatas.addAll(getResultDatas(type, datas));
                }
            }
        }
        return Pair.makePair(insertDatas, updateDatas);
    }

    protected ImmutableList<GovernanceData> getResultDatas(EventType type, List<GovernanceData> datas) {
        return FluentIterable.from(getCopy(datas)).transform(updateStatus(type))
                .transform(updateOperator(UserContainer.getUserName())).toList();
    }

    private static Function<GovernanceData, GovernanceData> updateStatus(final EventType type) {
        return new Function<GovernanceData, GovernanceData>() {
            @Override
            public GovernanceData apply(GovernanceData input) {
                return input.setStatus(EVENT_STATUS_MAP.get(type));
            }
        };
    }

    private static Function<GovernanceData, GovernanceData> updateOperator(final String username) {
        return new Function<GovernanceData, GovernanceData>() {
            @Override
            public GovernanceData apply(GovernanceData input) {
                return input.setLastOperator(username);
            }
        };
    }

    public static List<GovernanceData> getCopy(Iterable<GovernanceData> datas) {
        return FluentIterable.from(datas).transform(GET_COPY_FUNC).toList();
    }

    private static final Function<GovernanceData, GovernanceData> GET_COPY_FUNC = new Function<GovernanceData, GovernanceData>() {
        @Override
        public GovernanceData apply(GovernanceData input) {
            return input.copy();
        }
    };


    protected abstract Result deal(GovernanceData data, List<GovernanceData> relativeDatas, Status status);

    public class Result {

        private Map<EventType, List<GovernanceData>> datasChange = Maps.newHashMap();

        private RegisterAndUnRegisters datasToWrite = new RegisterAndUnRegisters();

        public Map<EventType, List<GovernanceData>> getDatasChange() {
            return datasChange;
        }

        public RegisterAndUnRegisters getDatasToWrite() {
            return datasToWrite;
        }

        public void insert(EventType type, GovernanceData data) {
            List<GovernanceData> changedDatas = datasChange.get(type);
            if (changedDatas == null) {
                changedDatas = Lists.newArrayList();
                datasChange.put(type, changedDatas);
            }
            changedDatas.add(data.copy());
        }

        public void addRegister(GovernanceData data) {
            datasToWrite.addRegister(data);
        }

        public void addUnRegister(GovernanceData data) {
            datasToWrite.addUnRegister(data);
        }
    }

    private void check(GovernanceData data, List<GovernanceData> relativeDatas, Status incomingState) {
        if (incomingState == Status.ENABLE) {
            check(data, relativeDatas);
        }
    }

    protected abstract void check(GovernanceData data, List<GovernanceData> relativeDatas);

    protected abstract List<GovernanceData> getRelativeDatas(GovernanceData data);

    @Override
    @Transactional
    public final void disable(GovernanceData data) {
        logger.info("disable data: {}", data);
        dealUserDataStatusChange(data, Status.DISABLE);
    }

    @Override
    @Transactional
    public final void delete(GovernanceData data) {
        logger.info("delete data: {}", data);
        if (data.getStatus() == Status.ENABLE) {
            logger.info("illegal delete to data in enable status: ({})", data);
            throw new EnableToDeleteException("illegal delete to data in enable status: (" + data + ")");
        }
        dealUserDataStatusChange(data, Status.DEL);
    }

    private void dealUserDataStatusChange(GovernanceData data, Status incomingStatus) {
        if (data.getStatus() == incomingStatus) {
            logger.info("{} with operator to same status {}", data, incomingStatus.getText());
            return;
        }

        List<GovernanceData> relativeDatas = getRelativeDatas(data);
        check(data, relativeDatas, incomingStatus);
        Result result = deal(data, relativeDatas, incomingStatus);
        writeDatasChange(result);
        writeZk(result);
    }

    private void writeZk(Result result) {
        try {
            zkClusterService.writeDatas(result.getDatasToWrite());
        } catch (RuntimeException e) {
            logger.error("", e);
            throw e;
        }
    }
}
