package ru.skillbox.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.skillbox.dto.enums.StatusCode;
import ru.skillbox.exception.UserNotFoundException;
import ru.skillbox.model.Friendship;
import ru.skillbox.model.Person;
import ru.skillbox.repository.FriendsRepository;
import ru.skillbox.response.data.PersonDto;

import java.util.*;

@Service
@AllArgsConstructor
@Slf4j
public class FriendsService {
    private final FriendsRepository friendsRepository;
    private final PersonService personService;


    //For Andrew
    public List<Long> getByCodePersonIdsForCurrentUser(StatusCode code) {
        long id = personService.getCurrentPerson().getId();
        Optional<List<Friendship>> friendsOptional = friendsRepository
                .findAllBySrcPersonIdOrDstPersonId(id, id);
        Set<Long> ids = new HashSet<>();
        ids.add(id);
        if (friendsOptional.isPresent()) {
            List<Friendship> friendshipList = friendsOptional.get();
            for (Friendship friendship : friendshipList) {
                if (friendship.getStatusCode().equals(code)) {
                    ids.add(friendship.getDstPerson().getId());
                    ids.add(friendship.getSrcPerson().getId());
                }
            }
        }
        return new ArrayList<>(ids);
    }


    public List<PersonDto> getRelationsForByCode(Long personId, StatusCode statusCode) {
        Optional<List<Friendship>> allByStatusCodeAndSrcPersonId =
                friendsRepository.findAllByStatusCodeAndSrcPersonId(statusCode, personId);
        ArrayList<PersonDto> result = new ArrayList<>();
        if (allByStatusCodeAndSrcPersonId.isPresent()) {
            PersonDto.PersonDtoBuilder builder = PersonDto.builder();
            List<Friendship> friendships = allByStatusCodeAndSrcPersonId.get(); //FixMe make pagination
            Person dstPerson;
            for (Friendship fr : friendships) {
                dstPerson = fr.getDstPerson();
                result.add(
                        builder
                                .id(dstPerson.getId())
                                .photo(dstPerson.getPhoto())
                                .statusCode(fr.getStatusCode().toString())
                                .firstName(dstPerson.getFirstName())
                                .lastName(dstPerson.getLastName())
                                .birthDate(dstPerson.getBirthDate())
                                .isOnline(dstPerson.getIsOnline())
                                //FixMe make normal
                                .city("dstPerson.getCity().toString()")
                                .country("dstPerson.getCountry().toString()")
                                .build());
            }
        }
        log.debug("getRelationsForByCode with id = {}, code = {}, result size = {}",
                personId, statusCode, result.size());
        return result;
    }

    //ToDo make logic to sending request "from other to current" person. some kind of approve
    public String sendFriendRequest(Long currentUser, Long otherPersonId) throws UserNotFoundException {
        Optional<Friendship> friendship =
                friendsRepository.findBySrcPersonIdAndDstPersonId(currentUser, otherPersonId);
        System.out.println(" ==> in sendFriendRequest ");
        if (friendship.isPresent()) {
            System.out.println("friendship.isPresent()");
            log.debug("friendship already exists {}", friendship.get());
        } else {
            Optional<Friendship> requestedFriendship = friendsRepository
                    .findByDstPersonIdAndSrcPersonIdAndStatusCodeIs(currentUser, otherPersonId, StatusCode.REQUEST_TO);
            Person otherPerson = personService.getPersonById(otherPersonId);
            Person currentPerson = personService.getPersonById(currentUser);
            if (requestedFriendship
                    .isEmpty()) {
                System.out.println("else if requestedFriendship" +
                        "                            .isEmpty()");
                Friendship f = new Friendship();
                f.setDstPerson(otherPerson);
                f.setSrcPerson(currentPerson);
                f.setStatusCode(StatusCode.REQUEST_TO);
                friendsRepository.save(f);

                Friendship fr = new Friendship();
                fr.setSrcPerson(otherPerson);
                fr.setDstPerson(currentPerson);
                fr.setStatusCode(StatusCode.REQUEST_FROM);
                friendsRepository.save(fr);

                log.debug("friendship saved {}", fr);
            } else {
                System.out.println("else");

            }
        }
        return "Ok";
    }

    public int getFriendsRequestsCountFor(Long personId) {
        return 5;
    }

    public String approveFriends(Long currentUser, Long otherPersonId) throws UserNotFoundException{
        Optional<Friendship> requestedFriendship = friendsRepository
                .findByDstPersonIdAndSrcPersonIdAndStatusCodeIs(currentUser, otherPersonId, StatusCode.REQUEST_TO);
        Optional<Friendship> alternativeFriendship = friendsRepository
                .findByDstPersonIdAndSrcPersonIdAndStatusCodeIs(otherPersonId, currentUser, StatusCode.REQUEST_FROM);
        if (requestedFriendship.isPresent() && alternativeFriendship.isPresent()) {
            Friendship fr = requestedFriendship.get();
            fr.setStatusCode(StatusCode.FRIEND);
            friendsRepository.save(fr);

            Friendship returnedFriendship = alternativeFriendship.get();
            returnedFriendship.setSrcPerson(fr.getDstPerson());
            returnedFriendship.setDstPerson(fr.getSrcPerson());
            returnedFriendship.setStatusCode(StatusCode.FRIEND);
            friendsRepository.save(returnedFriendship);

        }
        return "Ok";
    }

    public String deleteFriend(Long currentUser, Long otherPersonId) {
        Optional<Friendship> fr = friendsRepository.findBySrcPersonIdAndDstPersonId(currentUser, otherPersonId);
        Optional<Friendship> fr2 = friendsRepository.findBySrcPersonIdAndDstPersonId(otherPersonId, currentUser);
        if (fr.isPresent() && fr2.isPresent()) {
            Friendship friendship = fr.get();
            friendship.setPreviousStatus(friendship.getStatusCode());
            friendship.setStatusCode(StatusCode.NONE);
            friendsRepository.save(friendship);

            Friendship friendship2 = fr2.get();
            friendship2.setPreviousStatus(friendship2.getStatusCode());
            friendship2.setStatusCode(StatusCode.NONE);
            friendsRepository.save(friendship2);
            log.debug("friendship save = {}, {}",
                    currentUser, otherPersonId);
        } else {
            log.debug("friendship not present = {}, {}",
                    currentUser, otherPersonId);
        }
        return "Ok";
    }

    public String subscribe(Long currentUserId, Long otherPersonId) throws UserNotFoundException {
        Person currentPerson = personService.getPersonById(currentUserId);
        Person otherPerson = personService.getPersonById(otherPersonId);
        Friendship f = new Friendship();
        f.setDstPerson(otherPerson);
        f.setSrcPerson(currentPerson);
        f.setStatusCode(StatusCode.SUBSCRIBED);
        friendsRepository.save(f);
        return "Ok";
    }
}
