
DELIMITER //

CREATE TRIGGER verify_vote_time_before_insert
    BEFORE INSERT ON votes
    FOR EACH ROW
BEGIN
    DECLARE event_start DATETIME;
    DECLARE event_end DATETIME;

    -- Retrieve the start and end dates of the competition
    SELECT start_date, end_date INTO event_start, event_end
    FROM events
    WHERE event_id = NEW.event_id;

    -- Check if the vote is within the competition's start and end dates
    IF NEW.voted_at < event_start OR NEW.voted_at > event_end THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = NEW.vote_id;
    END IF;
END//

DELIMITER ;
