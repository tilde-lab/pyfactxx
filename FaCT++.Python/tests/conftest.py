"""Pytest fixtures"""

import pytest
import pyfactxx


@pytest.fixture
def reasoner():
    """
    Get instance of a reasoner.
    """
    return pyfactxx.Reasoner()
