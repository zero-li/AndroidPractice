# Endpoint
http://www.pjsip.org/docs/book-latest/html/endpoint.html

  Endpoint类是一个单例类，应用程序必须创建一个，最多只能创建一个这个类实例，
  然后它可以做任何事情，类似地，一旦这个类被销毁，应用程序就不能调用任何库API。
  这个类是PJSUA2的核心类，它提供了以下功能：
  * 启动和关闭
  * 配置的自定义，如核心UA（用户代理）SIP配置，媒体配置和日志记录配置
###### Instantiating the Endpoint
  ```
  public static Endpoint ep = new Endpoint();
  ```
###### Creating the Library
  ```
  try {
      // Creating the Library
      ep.libCreate();
  } catch (Exception e) {
      return;
  }
  ```
###### Initializing the Library and Configuring the Settings
EpConfig类提供了允许定制以下设置的端点配置：
* UAConfig，指定核心SIP用户代理设置。
* MediaConfig 来指定各种媒体全局设置
* LogConfig 来自定义日志设置。

请注意，有些设置可以在AccountConfig的基础上进一步指定。

要定制设置，请创建EpConfig类的实例，并在端点初始化期间指定它们（稍后将对此进行说明），例如：
```
private EpConfig epConfig = new EpConfig();

/* Override log level setting */
epConfig.getLogConfig().setLevel(LOG_LEVEL);
epConfig.getLogConfig().setConsoleLevel(LOG_LEVEL);

/* Set ua config. */
UaConfig ua_cfg = epConfig.getUaConfig();
ua_cfg.setUserAgent("Pjsua2 Android " + ep.libVersion().getFull());

/* Set media config. */
MediaConfig mediaConfig = epConfig.getMedConfig();

/* Init endpoint */
try {
    ep.libInit(epConfig);
} catch (Exception e) {
    return;
}

```

###### Creating One or More Transports
应用程序需要创建一个或多个传输才能发送或接收SIP消息：

```
private TransportConfig sipTpConfig = new TransportConfig();

/* Create transports. */
        try {
            ep.transportCreate(pjsip_transport_type_e.PJSIP_TRANSPORT_UDP,
                    sipTpConfig);
        } catch (Exception e) {
            System.out.println(e);
        }

        try {
            ep.transportCreate(pjsip_transport_type_e.PJSIP_TRANSPORT_TCP,
                    sipTpConfig);
        } catch (Exception e) {
            System.out.println(e);
        }
```
transportCreate（）方法返回新创建的传输ID，它使用传输类型和TransportConfig对象来自定义传输设置，如绑定地址和侦听端口号。 没有这个，默认情况下，传输将被绑定到INADDR_ANY和任何可用的端口。

除了创建无用户帐户（使用Account.create（））外，没有实际使用Transport ID，如后面将要解释的那样），并且如果应用程序需要，可能会显示传输列表给用户。

###### Creating A Secure Transport (TLS)
要创建TLS传输，可以使用与上面相同的方法。 您可以通过修改字段TransportConfig.tlsConfig来进一步自定义TLS传输，例如设置证书文件或选择使用的密码。
```
        try {
            sipTpConfig.setPort(SIP_PORT + 1);
            ep.transportCreate(pjsip_transport_type_e.PJSIP_TRANSPORT_TLS,
                    sipTpConfig);
        } catch (Exception e) {
            System.out.println(e);
        }

        /* Set SIP port back to default for JSON saved config */
        sipTpConfig.setPort(SIP_PORT);

```

###### Starting the Library
现在我们准备开始 library 了。 我们需要启动库来完成初始化阶段，例如 完成初始STUN地址解析，初始化/启动声音设备等。要启动库，请调用libStart（）方法：

```
        try {
            ep.libStart();
        } catch (Exception e) {
            return;
        }
```

###### Shutting Down the Library


