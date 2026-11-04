import os
from functools import lru_cache

import requests

from logger import logger


class Notifier:
    __NTFY_URL = os.environ.get("NTFY_URL", "https://ntfy.sh")
    __NTFY_TOPIC = os.environ["NTFY_TOPIC"]

    def notify(self, message: str) -> None:
        try:
            response = requests.post(
                url=f"{self.__NTFY_URL}/{self.__NTFY_TOPIC}",
                data=message.encode("utf-8"),
                headers={"Title": "Pod watcher report"},
                timeout=10,
            )
            response.raise_for_status()
            logger.info("ntfy: отправлено")
        except requests.RequestException as error:
            logger.error(f"ntfy: не отправлено: {type(error).__name__}")


@lru_cache
def get_notifier() -> Notifier:
    return Notifier()
