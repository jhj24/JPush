# JPush
```
public static void setDebugMode(boolean debug);
```

- 该接口需在 init 接口之前调用，避免出现部分日志没打印的情况
- debug 为 true 则会打印 debug 级别的日志，false 则只会打印 warning 级别以上的日志
```
public static void init(Context context);
```
- 初始化推送服务。
- 如果暂时不希望初始化 JPush SDK ，不要调用 init， 并且在应用初始化的时候就调用 stopPush

```
public static void stopPush(Context context);
```
- 停止推送服务
- 极光推送所有的其他 API 调用都无效，不能通过 JPushInterface.init 恢复，需要调用 resumePush 恢复。

```
public static void resumePush(Context context);
```
- 恢复推送服务。
- 调用了此 API 后，极光推送完全恢复正常工作。

```
public static boolean isPushStopped(Context context);
```
- 用来检查 Push Service 是否已经被停止
