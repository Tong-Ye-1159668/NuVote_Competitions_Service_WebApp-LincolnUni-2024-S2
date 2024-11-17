import json


class Response:
    def __init__(self, success: bool, message: str, data: any = None):
        self.success = success
        self.message = message
        self.data = data

    @staticmethod
    def success(message: str = 'Operation successful', data: any = None):
        return Response(True, message, data)

    @staticmethod
    def error(message: str = 'Operation failed', data: any = None):
        return Response(False, message, data)

    def to_json(self):
        return json.dumps(self.__dict__)

    def to_dict(self):
        return self.__dict__
